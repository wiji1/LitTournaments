package me.waterarchery.littournaments.handlers;

import me.waterarchery.litlibs.LitLibs;
import me.waterarchery.litlibs.configuration.ConfigManager;
import me.waterarchery.littournaments.LitTournaments;
import me.waterarchery.littournaments.configurations.ConfigFile;
import me.waterarchery.littournaments.configurations.LangFile;
import me.waterarchery.littournaments.configurations.LeaderboardMenuFile;
import me.waterarchery.littournaments.configurations.TournamentMenuFile;

import java.io.File;

public class FileHandler {

    private static ConfigManager config;
    private static ConfigManager lang;
    private static ConfigManager tournamentMenu;
    private static ConfigManager leaderboardMenu;

    public static void load() {
        LitLibs libs = LitTournaments.getLitLibs();
        reload();
        libs.reload();
    }

    public static void reload() {
        LitLibs libs = LitTournaments.getLitLibs();

        config = new ConfigFile(libs, "", "config", true);
        config = new ConfigFile(libs, "", "config", true);
        lang = new LangFile(libs, "lang", "en", true);
        lang = new LangFile(libs, "lang", "en", true);
        tournamentMenu = new TournamentMenuFile(libs, "menu", "tournaments", true);
        tournamentMenu = new TournamentMenuFile(libs, "menu", "tournaments", true);
        leaderboardMenu = new LeaderboardMenuFile(libs, "menu", "leaderboard", true);
        leaderboardMenu = new LeaderboardMenuFile(libs, "menu", "leaderboard", true);
        generateTournamentFiles();
    }

    public static ConfigManager getConfig() {
        return config;
    }

    public static ConfigManager getLang() {
        return lang;
    }

    public static ConfigManager getTournamentMenu() { return tournamentMenu; }

    public static ConfigManager getLeaderboardMenu() { return leaderboardMenu; }

    public static void generateTournamentFiles() {
        LitTournaments instance = LitTournaments.getInstance();
        File tournamentsFolder = new File(instance.getDataFolder(), "/tournaments");

        File[] contents = tournamentsFolder.listFiles();
        if (contents != null) return;

        if (!tournamentsFolder.exists()) tournamentsFolder.mkdir();

        LoadHandler.getInstance().saveFiles();
    }

}