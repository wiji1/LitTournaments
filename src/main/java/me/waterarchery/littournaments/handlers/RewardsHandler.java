package me.waterarchery.littournaments.handlers;

import me.waterarchery.littournaments.LitTournaments;
import me.waterarchery.littournaments.models.RewardInstance;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RewardsHandler {

	private static RewardsHandler instance;
	private final List<RewardInstance> rewardInstances;

	public static RewardsHandler getInstance() {
		if (instance == null) instance = new RewardsHandler();
		return instance;
	}

	private RewardsHandler() {
		rewardInstances = new ArrayList<>();
	}

	public List<RewardInstance> getRewardInstances() {
		return rewardInstances;
	}

	public void giveCommandReward(UUID playerUUID, String command) {
		Player player = Bukkit.getPlayer(playerUUID);

		if (player != null && player.isOnline()) {
			dispatchCommand(player, command);
			return;
		}

		rewardInstances.add(new RewardInstance(playerUUID, command));
	}

	public void dispatchCommand(Player player, String command) {
		String finalCommand = command.replace("%player%", player.getName());

		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
			}
		}.runTask(LitTournaments.getInstance());
	}
}
