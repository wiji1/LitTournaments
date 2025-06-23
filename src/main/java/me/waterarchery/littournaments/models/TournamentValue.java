package me.waterarchery.littournaments.models;

import me.waterarchery.littournaments.LitTournaments;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class TournamentValue {

    private final UUID uuid;
    private final long value;

    public TournamentValue(UUID uuid, long value) {
        this.uuid = uuid;
        this.value = value;
    }

    public String getName() {
        try {
            String cachedName = LitTournaments.getDatabase().getPlayerName(uuid).get();
            if (cachedName != null) return cachedName;
        } catch (Exception e) {
            LitTournaments.getLitLibs().getLogger().error("Could not fetch player name from database cache: " + uuid);
        }

        return "ERROR";
    }

    public UUID getUUID() {
        return uuid;
    }

    public long getValue() {
        return value;
    }

}
