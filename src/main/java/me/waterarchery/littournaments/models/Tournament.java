package me.waterarchery.littournaments.models;


import me.waterarchery.littournaments.LitTournaments;
import me.waterarchery.littournaments.api.events.TournamentEndEvent;
import me.waterarchery.littournaments.api.events.TournamentStartEvent;
import me.waterarchery.littournaments.database.Database;
import me.waterarchery.littournaments.handlers.TournamentHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.*;
import java.util.concurrent.CompletableFuture;

public class Tournament {

    private final String identifier;
    private final YamlConfiguration yamlConfiguration;
    private final boolean isActive;
    private final String timePeriod;
    private BukkitTask finishTask;
    private JoinChecker joinChecker;
    private ActionChecker actionChecker;
    private TournamentLeaderboard leaderboard;

    public Tournament(String identifier, YamlConfiguration yamlConfiguration) {
        this.identifier = identifier;
        this.yamlConfiguration = yamlConfiguration;

        this.isActive = yamlConfiguration.getBoolean("Active");
        this.timePeriod = yamlConfiguration.getString("TimePeriod");

        load();
    }

    public void load() {
        joinChecker = new JoinChecker(yamlConfiguration, this);
        actionChecker = new ActionChecker(yamlConfiguration, this);
        leaderboard = new TournamentLeaderboard(this);
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
            return now.atTime(23,59);
        }
        else if (timePeriod.equalsIgnoreCase("weekly")) {
            LocalDate endOfWeekDate = now.with(DayOfWeek.SUNDAY);
            LocalTime endOfDayTime = LocalTime.of(23, 59, 59);

            return LocalDateTime.of(endOfWeekDate, endOfDayTime);
        }
        else if (timePeriod.equalsIgnoreCase("monthly")) {
            int lastDayOfMonth = now.lengthOfMonth();
            return now.withDayOfMonth(lastDayOfMonth).atTime(23,59);
        }

        return null;
    }

    public Duration getRemainingTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime finishTime = getFinishTime();
        return Duration.between(now, finishTime);
    }

    public void startFinishTask() {
        if (finishTask != null) finishTask.cancel();

        LocalDateTime finishTime = getFinishTime();
        LocalDateTime now = LocalDateTime.now();
        Duration remaining = Duration.between(now, finishTime);
        long inTicks = remaining.getSeconds() * 20L;

        finishTask = new BukkitRunnable() {
            @Override
            public void run() {
                finishTournament();
            }
        }.runTaskLaterAsynchronously(LitTournaments.getInstance(), inTicks);
    }

    public void finishTournament() {
        Tournament tournament = this;
        Database database = LitTournaments.getDatabase();
        TournamentHandler tournamentHandler = TournamentHandler.getInstance();
        tournamentHandler.parseConditionalCommand(tournament, "TOURNAMENT_END");

        CompletableFuture.runAsync(database.getReloadTournamentRunnable(tournament))
                .thenRun(() -> {
                    TournamentEndEvent tournamentEndEvent = new TournamentEndEvent(tournament);
                    Bukkit.getPluginManager().callEvent(tournamentEndEvent);

                    database.clearTournament(tournament);
                    getLeaderboard().clear();
                    startFinishTask();

                    tournamentHandler.parseRewards(tournament);

                    TournamentStartEvent tournamentStartEvent = new TournamentStartEvent(tournament);
                    Bukkit.getPluginManager().callEvent(tournamentStartEvent);
                    tournamentHandler.parseConditionalCommand(tournament, "TOURNAMENT_START");
                });
    }

    public String getIdentifier() { return identifier; }

    public YamlConfiguration getYamlConfiguration() { return yamlConfiguration; }

    public JoinChecker getJoinChecker() { return joinChecker; }

    public ActionChecker getActionChecker() { return actionChecker; }

    public TournamentLeaderboard getLeaderboard() { return leaderboard; }

    public boolean isActive() { return isActive; }

    public String getTimePeriod() { return timePeriod; }

}
