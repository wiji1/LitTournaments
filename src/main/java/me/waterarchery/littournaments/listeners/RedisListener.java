package me.waterarchery.littournaments.listeners;

import me.waterarchery.littournaments.api.events.RedisUpdateEvent;
import me.waterarchery.littournaments.enums.RedisMessage;
import me.waterarchery.littournaments.handlers.TournamentHandler;
import me.waterarchery.littournaments.models.Tournament;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

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

	}
}
