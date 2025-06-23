package me.waterarchery.littournaments.listeners;

import me.waterarchery.littournaments.LitTournaments;
import me.waterarchery.littournaments.handlers.PlayerHandler;
import me.waterarchery.littournaments.handlers.RewardsHandler;
import me.waterarchery.littournaments.models.RewardInstance;
import me.waterarchery.littournaments.models.TournamentPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinLeaveListeners implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerHandler playerHandler = PlayerHandler.getInstance();
        Player player = event.getPlayer();

        TournamentPlayer tournamentPlayer = new TournamentPlayer(player.getUniqueId());
        playerHandler.addPlayer(tournamentPlayer);
        playerHandler.initializePlayer(tournamentPlayer, true);

        LitTournaments.getDatabase().setPlayerName(player);

        LitTournaments.getDatabase().getRewardCommands(player.getUniqueId()).thenAccept(
            rewardCommands -> {
                for (String rewardCommand : rewardCommands) {
                    RewardsHandler.getInstance().dispatchCommand(player, rewardCommand);
                }

                if (!rewardCommands.isEmpty()) {
                    LitTournaments.getDatabase().clearRewards(player.getUniqueId());
                }
            }
        ).join();
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLeave(PlayerJoinEvent event) {
        PlayerHandler playerHandler = PlayerHandler.getInstance();
        Player player = event.getPlayer();

        TournamentPlayer tournamentPlayer = playerHandler.getPlayer(player.getUniqueId());
        playerHandler.removePlayer(tournamentPlayer);
    }

}
