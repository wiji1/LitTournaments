package me.waterarchery.littournaments.handlers;

import me.waterarchery.litlibs.LitLibs;
import me.waterarchery.litlibs.logger.Logger;
import me.waterarchery.littournaments.LitTournaments;
import me.waterarchery.littournaments.database.Database;
import me.waterarchery.littournaments.database.MySQL;
import me.waterarchery.littournaments.database.SQLite;
import me.waterarchery.littournaments.hooks.PlaceholderAPIHook;
import me.waterarchery.littournaments.listeners.JoinLeaveListeners;
import me.waterarchery.littournaments.listeners.tournamentListeners.*;
import me.waterarchery.littournaments.models.Tournament;
import me.waterarchery.littournaments.models.TournamentPlayer;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class LoadHandler {

    private static LoadHandler instance;

    public static LoadHandler getInstance() {
        if (instance == null) instance = new LoadHandler();
        return instance;
    }

    private LoadHandler() { }

    public void load() {
        LitLibs libs = LitTournaments.getLitLibs();
        Logger logger = libs.getLogger();

        logger.log("Loading files");
        FileHandler.load();

        logger.log("Registering events, commands and hooks");
        registerEvents();
        registerCommands();
        registerHooks();

        logger.log("Loading tournaments");
        TournamentHandler tournamentHandler = TournamentHandler.getInstance();
        tournamentHandler.reloadTournaments();

        logger.log("Loading database");
        loadDatabase();
    }

    public void loadDatabase() {
        TournamentHandler tournamentHandler = TournamentHandler.getInstance();
        LitTournaments instance = LitTournaments.getInstance();
        LitLibs libs = LitTournaments.getLitLibs();
        Logger logger = libs.getLogger();
        FileConfiguration yml = FileHandler.getConfig().getYml();
        List<Tournament> tournaments = tournamentHandler.getTournaments();

        String databaseType = yml.getString("Database.DatabaseType", "sqlite");

        Database database;
        if (databaseType.equalsIgnoreCase("sqlite")) {
            database = new SQLite(instance);
            database.initialize();
            LitTournaments.setDatabase(database);
        }
        else if (databaseType.equalsIgnoreCase("mysql")) {
            database = new MySQL(instance);
            LitTournaments.setDatabase(database);
        }
        else {
            logger.error("You made a mistake while configuring DatabaseType. Please set it to SQLite or MySQL");
            instance.getPluginLoader().disablePlugin(instance);
            return;
        }

        loadPlayers();

        database.load(tournaments);
    }

    public void loadPlayers() {
        PlayerHandler playerHandler = PlayerHandler.getInstance();

        for (Player player : Bukkit.getOnlinePlayers()) {
            TournamentPlayer tournamentPlayer = new TournamentPlayer(player.getUniqueId());
            playerHandler.addPlayer(tournamentPlayer);
            playerHandler.initializePlayer(tournamentPlayer, false);
        }
    }

    public void registerEvents() {
        LitTournaments instance = LitTournaments.getInstance();

        instance.getServer().getPluginManager().registerEvents(new JoinLeaveListeners(), instance);

        // Tournaments
        instance.getServer().getPluginManager().registerEvents(new BlockBreakListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new BlockPlaceListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new ItemCraftListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new MobKillListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new ItemBreakListener(), instance);
    }

    public void registerCommands() {
        CommandHandler commandHandler = CommandHandler.getInstance();

        commandHandler.registerSuggestions();
        commandHandler.registerMessages();
        commandHandler.registerCommands();
    }

    public void registerHooks() {
        LitLibs libs = LitTournaments.getLitLibs();
        Logger logger = libs.getLogger();

        // bStats
        new Metrics(LitTournaments.getInstance(), 22957);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            logger.log("Found PlaceHolderAPI hook");
            PlaceholderAPIHook placeholderAPIHook = new PlaceholderAPIHook();
            placeholderAPIHook.register();
        }
    }

    public void saveFiles() {
        final String path = "tournaments";
        final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        final LitLibs litLibs = LitTournaments.getLitLibs();
        final Logger logger = litLibs.getLogger();
        final LitTournaments instance = LitTournaments.getInstance();

        try {
            final JarFile jar = new JarFile(jarFile);
            final Enumeration<JarEntry> entries = jar.entries();
            while(entries.hasMoreElements()) {
                final String name = entries.nextElement().getName();
                if (name.startsWith(path + "/") && !name.endsWith("tournaments/")) {
                    instance.saveResource(name, false);
                    logger.log("Generated new tournament file: " + name);
                }
            }
            jar.close();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
