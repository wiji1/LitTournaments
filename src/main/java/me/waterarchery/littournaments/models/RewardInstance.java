package me.waterarchery.littournaments.models;

import me.waterarchery.littournaments.LitTournaments;
import me.waterarchery.littournaments.enums.RedisMessage;
import me.waterarchery.littournaments.handlers.RedisHandler;
import me.waterarchery.littournaments.handlers.RewardsHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;
import java.util.function.Consumer;

public class RewardInstance {
	private final UUID transactionId;
	private final UUID player;
	private final String command;
	private final Consumer<String> responseHandler;
	private final BukkitTask timeoutTask;

	public RewardInstance(UUID player, String command) {
		this.transactionId = UUID.randomUUID();
		this.player = player;
		this.command = command;

		responseHandler = response -> {
			if (response == null || response.isEmpty() || !response.equals(transactionId.toString())) return;

			deregister();
		};

		timeoutTask = new BukkitRunnable() {
			@Override
			public void run() {
				enterToDatabase();
			}
		}.runTaskLater(LitTournaments.getInstance(), 20 * 2);

		if (!RedisHandler.getInstance().isEnabled()) {
			enterToDatabase();
			return;
		}

		send();
	}

	public UUID getTransactionId() {
		return transactionId;
	}

	public UUID getPlayer() {
		return player;
	}

	public String getCommand() {
		return command;
	}

	public Consumer<String> getResponseHandler() {
		return responseHandler;
	}

	public void send() {
		RedisHandler.getInstance().sendUpdate(RedisMessage.REWARD_PLAYER_QUERY, transactionId.toString(), player.toString(), command);
	}

	private void enterToDatabase() {
		LitTournaments.getDatabase().addReward(this);
		deregister();
	}

	private void deregister() {
		RewardsHandler.getInstance().getRewardInstances().remove(this);
		if (timeoutTask != null && !timeoutTask.isCancelled()) timeoutTask.cancel();
	}
}