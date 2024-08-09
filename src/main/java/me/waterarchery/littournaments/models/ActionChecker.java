package me.waterarchery.littournaments.models;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class ActionChecker {

    private final Tournament tournament;
    private final List<String> actionWhitelist;
    private final List<String> actionBlacklist;
    private final List<String> worldWhitelist;
    private final List<String> worldBlacklist;

    public ActionChecker(YamlConfiguration yaml, Tournament tournament) {
        this.tournament = tournament;
        actionWhitelist = yaml.getStringList("Checkers.Action.Whitelist");
        actionBlacklist = yaml.getStringList("Checkers.Action.Blacklist");
        worldWhitelist = yaml.getStringList("Checkers.World.Whitelist");
        worldBlacklist = yaml.getStringList("Checkers.World.Blacklist");
    }

    public List<String> getActionWhitelist() {
        return actionWhitelist;
    }

    public List<String> getActionBlacklist() {
        return actionBlacklist;
    }

    public List<String> getWorldWhitelist() {
        return worldWhitelist;
    }

    public List<String> getWorldBlacklist() {
        return worldBlacklist;
    }

}
