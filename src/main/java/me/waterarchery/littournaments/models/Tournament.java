package me.waterarchery.littournaments.models;


import me.waterarchery.littournaments.LitTournaments;
import me.waterarchery.littournaments.api.events.TournamentEndEvent;
import me.waterarchery.littournaments.api.events.TournamentStartEvent;
import me.waterarchery.littournaments.database.Database;
import me.waterarchery.littournaments.enums.RedisMessage;
import me.waterarchery.littournaments.handlers.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.time.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class Tournament {

    private final String identifier;
    private final YamlConfiguration yamlConfiguration;
    private final boolean shouldRestartAfterFinished;
    private boolean isActive;
    private boolean isEnding;
    private final String timePeriod;
    private final String coolName;
    private BukkitTask finishTask;
    private JoinChecker joinChecker;
    private ActionChecker actionChecker;
    private TournamentLeaderboard leaderboard;

    public Tournament(String identifier, YamlConfiguration yamlConfiguration) {
        this.identifier = identifier;
        this.yamlConfiguration = yamlConfiguration;

        this.isActive = yamlConfiguration.getBoolean("Active");
        this.isEnding = false;
        this.timePeriod = yamlConfiguration.getString("TimePeriod");
        this.shouldRestartAfterFinished = yamlConfiguration.getBoolean("RestartAfterFinished", true);

        this.coolName = yamlConfiguration.getString("CoolName", identifier);

        load();
    }

    public void load() {
        joinChecker = new JoinChecker(yamlConfiguration, this);
        actionChecker = new ActionChecker(yamlConfiguration, this);
        leaderboard = new TournamentLeaderboard(this);

        if (isActive)
            startFinishTask();
    }

    public boolean checkWorldEnabled(String worldName) {
        if (actionChecker.getWorldWhitelist().contains(worldName)) return true;
        else {
            if (actionChecker.getWorldWhitelist().contains("*")) {
                return !actionChecker.getWorldBlacklist().contains(worldName);
            }

            return false;
        }
    }

    public boolean checkActionAllowed(String actionName) {
        if (actionChecker.getActionWhitelist().contains(actionName)) return true;
        else {
            if (actionChecker.getActionWhitelist().contains("*")) {
                return !actionChecker.getActionBlacklist().contains(actionName);
            }

            return false;
        }
    }

    public LocalDateTime getFinishTime() {
        LocalDate now = LocalDate.now();

        if (timePeriod.equalsIgnoreCase("daily")) {
            return now.atTime(23,59, 59);
        }
        else if (timePeriod.equalsIgnoreCase("weekly")) {
            LocalDate endOfWeekDate = now.with(DayOfWeek.SUNDAY);
            LocalTime endOfDayTime = LocalTime.of(23, 59, 59);

            return LocalDateTime.of(endOfWeekDate, endOfDayTime);
        }
        else if (timePeriod.equalsIgnoreCase("monthly")) {
            int lastDayOfMonth = now.lengthOfMonth();
            return now.withDayOfMonth(lastDayOfMonth).atTime(23,59, 59);
        }

        return null;
    }

    public Duration getRemainingTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime finishTime = getFinishTime();
        return Duration.between(now, finishTime);
    }

    public void startFinishTask() {
        stopFinishTask();

        LocalDateTime finishTime = getFinishTime();
        LocalDateTime now = LocalDateTime.now();
        Duration remaining = Duration.between(now, finishTime);
        long inTicks = remaining.getSeconds() * 20L;

        finishTask = Bukkit.getScheduler().runTaskLater(LitTournaments.getInstance(), this::finishTournament, inTicks);
    }

    public void stopFinishTask() {
        if (finishTask != null) finishTask.cancel();
    }

    public void finishTournament() {
        Tournament tournament = this;

        if(!tournament.isActive() || tournament.isEnding()) return;
        tournament.isEnding = true;

        Database database = LitTournaments.getDatabase();
        TournamentHandler tournamentHandler = TournamentHandler.getInstance();
        PlayerHandler playerHandler = PlayerHandler.getInstance();
        tournamentHandler.parseConditionalCommand(tournament, "TOURNAMENT_END");

        TournamentEndEvent tournamentEndEvent = new TournamentEndEvent(tournament);
        Bukkit.getPluginManager().callEvent(tournamentEndEvent);
        WebhookHandler.sendWebhook(tournament);
        int waitTime = FileHandler.getConfig().getYml().getInt("WaitTimeBetweenTournaments");

        RedisHandler redisHandler = RedisHandler.getInstance();
        redisHandler.sendUpdate(RedisMessage.TOURNAMENT_END, tournament.getIdentifier());

        CompletableFuture.runAsync(database.getReloadTournamentRunnable(tournament))
                .thenRun(() -> {
                    if (shouldRestartAfterFinished) {
                        tournamentHandler.parseRewards(tournament);
                        database.clearTournament(tournament);
                        playerHandler.clearPlayerValues(tournament);
                        getLeaderboard().clear();
                        stopFinishTask();
                    }

                    File file = new File(LitTournaments.getInstance().getDataFolder(), "/tournaments/" + this.identifier + ".yml");
                    yamlConfiguration.set("Active", false);
                    try {
                        yamlConfiguration.save(file);
                    } catch (IOException e) {
                        LitTournaments.getInstance().getLogger().log(Level.WARNING, "Error saving tournament file: " + this.identifier, e);
                    }

                    this.isActive = false;
                    tournamentHandler.parseRewards(tournament);
                    stopFinishTask();
                }).thenRun(() -> {
                    if (!shouldRestartAfterFinished) return;

                    Bukkit.getScheduler().runTaskLater(LitTournaments.getInstance(), this::startTournament, waitTime * 20L);
                });
    }

    public void startTournament() {
        if (isActive()) return;

        TournamentHandler tournamentHandler = TournamentHandler.getInstance();
        Database database = LitTournaments.getDatabase();
        PlayerHandler playerHandler = PlayerHandler.getInstance();

        startFinishTask();
        database.clearTournament(this);
        playerHandler.clearPlayerValues(this);
        getLeaderboard().clear();

        TournamentStartEvent tournamentStartEvent = new TournamentStartEvent(this);
        Bukkit.getPluginManager().callEvent(tournamentStartEvent);
        tournamentHandler.parseConditionalCommand(this, "TOURNAMENT_START");
        this.isActive = true;
        this.isEnding = false;

        RedisHandler redisHandler = RedisHandler.getInstance();
        redisHandler.sendUpdate(RedisMessage.TOURNAMENT_START, this.getIdentifier());

        File file = new File(LitTournaments.getInstance().getDataFolder(), "/tournaments/" + this.identifier + ".yml");
        yamlConfiguration.set("Active", true);
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            LitTournaments.getInstance().getLogger().log(Level.WARNING, "Error saving tournament file: " + this.identifier, e);
        }
    }

    public String getCoolName() { return coolName; }

    public String getIdentifier() { return identifier; }

    public YamlConfiguration getYamlConfiguration() { return yamlConfiguration; }

    public JoinChecker getJoinChecker() { return joinChecker; }

    public ActionChecker getActionChecker() { return actionChecker; }

    public TournamentLeaderboard getLeaderboard() { return leaderboard; }

    public boolean isActive() { return isActive; }

    public boolean isEnding() { return isEnding; }

    public String getTimePeriod() { return timePeriod; }

}
