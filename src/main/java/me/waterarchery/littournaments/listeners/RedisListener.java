package me.waterarchery.littournaments.listeners;

import me.waterarchery.littournaments.api.events.RedisUpdateEvent;
import me.waterarchery.littournaments.enums.RedisMessage;
import me.waterarchery.littournaments.handlers.RedisHandler;
import me.waterarchery.littournaments.handlers.RewardsHandler;
import me.waterarchery.littournaments.handlers.TournamentHandler;
import me.waterarchery.littournaments.models.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public class RedisListener implements Listener {

	@EventHandler
	public void onRedisMessage(RedisUpdateEvent event) {
		RedisMessage messageType = event.getMessageType();
		List<String> args = event.getArgs();


		if (messageType == RedisMessage.TOURNAMENT_END) {
			Tournament tournament = TournamentHandler.getInstance().getTournament(args.get(0));
			if (tournament != null) tournament.finishTournament();
		}

		if (messageType == RedisMessage.TOURNAMENT_START) {
			Tournament tournament = TournamentHandler.getInstance().getTournament(args.get(0));
			if (tournament != null) tournament.startTournament();
		}

		if (messageType == RedisMessage.REWARD_PLAYER_QUERY) {
			UUID responseId = UUID.fromString(args.get(0));
			UUID playerUUID = UUID.fromString(args.get(1));
			String command = String.join(" ", args.subList(2, args.size()));

			Player player = Bukkit.getPlayer(playerUUID);
			if (player == null || !player.isOnline()) return;

			RewardsHandler.getInstance().dispatchCommand(player, command);
			RedisHandler.getInstance().sendUpdate(RedisMessage.REWARD_PLAYER_RESPONSE, responseId.toString());
		}

		if (messageType == RedisMessage.REWARD_PLAYER_RESPONSE) {
			UUID responseId = UUID.fromString(args.get(0));

			RewardsHandler.getInstance().getRewardInstances().stream()
				.filter(rewardInstance -> rewardInstance.getTransactionId().equals(responseId))
				.findFirst()
				.ifPresent(rewardInstance -> rewardInstance.getResponseHandler().accept(responseId.toString()));
		}

	}
}
