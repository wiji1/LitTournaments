package me.waterarchery.littournaments.listeners.tournamentListeners;

import me.waterarchery.littournaments.LitTournaments;
import me.waterarchery.littournaments.handlers.PointHandler;
import me.waterarchery.littournaments.handlers.TournamentHandler;
import me.waterarchery.littournaments.models.Tournament;
import me.waterarchery.littournaments.models.tournaments.PlayTimeTournament;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class PlayTimeListener {

    private static PlayTimeListener instance;
    private BukkitTask task;

    public synchronized static PlayTimeListener getInstance() {
        if (instance == null) instance = new PlayTimeListener();
        return instance;
    }

    private PlayTimeListener() {
        PointHandler pointHandler = PointHandler.getInstance();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                TournamentHandler tournamentHandler = TournamentHandler.getInstance();
                List<Tournament> tournaments = tournamentHandler.getTournaments(PlayTimeTournament.class);

                for (Tournament tournament : tournaments) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        World world = player.getWorld();
                        pointHandler.addPoint(player.getUniqueId(), tournament, world.getName(), "none", 1);
                    }
                }
            }
        }.runTaskTimerAsynchronously(LitTournaments.getInstance(), 20 * 60, 20 * 60);
    }

    public BukkitTask getTask() {
        return task;
    }

}
