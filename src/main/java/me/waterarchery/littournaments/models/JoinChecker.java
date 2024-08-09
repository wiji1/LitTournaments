package me.waterarchery.littournaments.models;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class JoinChecker {

    private final Tournament tournament;
    private final boolean autoJoinEnabled;
    private final boolean autoJoinRequirePermission;
    private final boolean messageOnAutoJoin;
    private final boolean joinRequirePermission;

    public JoinChecker(YamlConfiguration yaml, Tournament tournament) {
        this.tournament = tournament;
        autoJoinEnabled = yaml.getBoolean("JoinChecker.AutoJoin.Enabled");
        autoJoinRequirePermission = yaml.getBoolean("JoinChecker.AutoJoin.RequirePermission");
        joinRequirePermission = yaml.getBoolean("JoinChecker.Join.RequirePermission");
        messageOnAutoJoin = yaml.getBoolean("JoinChecker.Join.MessageOnAutoJoin");
    }

    public boolean canJoin(UUID uuid) {
        if (isJoinRequirePermission()) {
            Player player = Bukkit.getPlayer(uuid);
            return player != null && player.hasPermission("littournaments.join." + tournament.getIdentifier());
        }

        return true;
    }

    public boolean isAutoJoinEnabled() {
        return autoJoinEnabled;
    }

    public boolean isAutoJoinRequirePermission() {
        return autoJoinRequirePermission;
    }

    public boolean isJoinRequirePermission() {
        return joinRequirePermission;
    }

    public boolean isMessageOnAutoJoin() {
        return messageOnAutoJoin;
    }

}
