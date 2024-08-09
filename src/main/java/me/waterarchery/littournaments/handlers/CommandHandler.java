package me.waterarchery.littournaments.handlers;

import com.google.common.collect.Lists;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import me.waterarchery.litlibs.LitLibs;
import me.waterarchery.litlibs.configuration.ConfigManager;
import me.waterarchery.litlibs.handlers.MessageHandler;
import me.waterarchery.litlibs.handlers.SoundHandler;
import me.waterarchery.littournaments.LitTournaments;
import me.waterarchery.littournaments.commands.TournamentCommand;
import me.waterarchery.littournaments.models.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandHandler {

    private static CommandHandler instance;
    private TournamentCommand tournamentCommand;
    private final BukkitCommandManager<CommandSender> manager;

    public static CommandHandler getInstance() {
        if (instance == null) instance = new CommandHandler();
        return instance;
    }

    private CommandHandler() {
        LitTournaments instance = LitTournaments.getInstance();
        manager = BukkitCommandManager.create(instance);
    }

    public void registerCommands() {
        tournamentCommand = new TournamentCommand();
        manager.registerCommand(tournamentCommand);
    }

    public void unRegisterCommands() {
        if (manager != null) {
            manager.unregisterCommands(tournamentCommand);
            Lists.newArrayList("littournaments", "tournaments", "tournament").forEach(this::unregisterCommand);
        }
    }

    private void unregisterCommand(String name) {
        getBukkitCommands(getCommandMap()).remove(name);
    }

    @NotNull
    private CommandMap getCommandMap() {
        try {
            final Server server = Bukkit.getServer();
            final Method getCommandMap = server.getClass().getDeclaredMethod("getCommandMap");
            getCommandMap.setAccessible(true);

            return (CommandMap) getCommandMap.invoke(server);
        } catch (final Exception ignored) {
            throw new CommandRegistrationException("Unable get Command Map. Commands will not be registered!");
        }
    }

    // copied from triumph-cmd, credit goes to triumph-team
    @NotNull
    private Map<String, Command> getBukkitCommands(@NotNull final CommandMap commandMap) {
        try {
            final Field bukkitCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            bukkitCommands.setAccessible(true);
            //noinspection unchecked
            return (Map<String, Command>) bukkitCommands.get(commandMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new CommandRegistrationException("Unable get Bukkit commands. Commands might not be registered correctly!");
        }
    }

    public void registerSuggestions() {
        manager.registerSuggestion(SuggestionKey.of("players"), (sender, context) -> {
            List<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                players.add(player.getName());
            }
            return players;
        });

        manager.registerSuggestion(SuggestionKey.of("tournaments"), (sender, context) -> {
            TournamentHandler tournamentHandler = TournamentHandler.getInstance();
            List<String> tournaments = new ArrayList<>();

            for (Tournament tournament : tournamentHandler.getTournaments()) {
                tournaments.add(tournament.getIdentifier());
            }

            return tournaments;
        });
    }

    public void registerMessages() {
        LitLibs libs = LitTournaments.getLitLibs();
        MessageHandler mHandler = libs.getMessageHandler();
        ConfigManager lang = FileHandler.getLang();
        SoundHandler soundHandler = libs.getSoundHandler();

        manager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
            String message = lang.getString("TooFewArgs");
            mHandler.sendMessage(sender, message);
            if (sender instanceof Player) soundHandler.sendSound((Player) sender, "Sounds.InvalidCommand");
        });

        manager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> {
            String message = lang.getString("TooManyArgs");
            mHandler.sendMessage(sender, message);
            if (sender instanceof Player) soundHandler.sendSound((Player) sender, "Sounds.InvalidCommand");
        });

        manager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> {
            String message = lang.getString("InvalidArg");
            mHandler.sendMessage(sender, message);
            if (sender instanceof Player) soundHandler.sendSound((Player) sender, "Sounds.InvalidCommand");
        });

        manager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> {
            String message = lang.getString("UnknownCommand");
            mHandler.sendMessage(sender, message);
            if (sender instanceof Player) soundHandler.sendSound((Player) sender, "Sounds.InvalidCommand");
        });

        manager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> {
            String message = lang.getString("NoPermission");
            mHandler.sendMessage(sender, message);
            if (sender instanceof Player) soundHandler.sendSound((Player) sender, "Sounds.NoPermission");
        });
    }

}
