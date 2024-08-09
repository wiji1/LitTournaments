package me.waterarchery.littournaments.handlers;


import me.waterarchery.litlibs.LitLibs;
import me.waterarchery.littournaments.LitTournaments;
import me.waterarchery.littournaments.database.Database;
import me.waterarchery.littournaments.models.Tournament;
import me.waterarchery.littournaments.models.TournamentPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PointHandler {

    private static PointHandler instance;

    public static PointHandler getInstance() {
        if (instance == null) instance = new PointHandler();
        return instance;
    }

    private PointHandler() { }

    public void addPoint(UUID uuid, Tournament tournament, String worldName, String actionName) {
        if (tournament.checkWorldEnabled(worldName) && tournament.checkActionAllowed(actionName)) {
            addPoint(uuid, tournament);
        }
    }

    public void addPoint(UUID uuid, Tournament tournament) {
        if (!tournament.isActive()) return;

        PlayerHandler playerHandler = PlayerHandler.getInstance();
        TournamentPlayer tournamentPlayer = playerHandler.getPlayer(uuid);
        LitLibs libs = LitTournaments.getLitLibs();

        if (tournamentPlayer.isRegistered(tournament)) {
            Database database = LitTournaments.getDatabase();
            database.addPoint(uuid, tournament, 1);

            HashMap<Tournament, Long> map = tournamentPlayer.getTournamentValueMap();
            long currentPoint = map.getOrDefault(tournament, 0L);
            map.replace(tournament, currentPoint + 1);
        }
        else if (tournamentPlayer.isLoading()) {
            Player player = Bukkit.getPlayer(uuid);
            libs.getMessageHandler().sendLangMessage(player, "StillLoading");
        }
    }

}
