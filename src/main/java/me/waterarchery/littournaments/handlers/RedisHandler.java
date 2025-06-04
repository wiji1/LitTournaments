package me.waterarchery.littournaments.handlers;

import me.waterarchery.litlibs.logger.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import me.waterarchery.littournaments.LitTournaments;
import me.waterarchery.littournaments.api.events.RedisUpdateEvent;
import me.waterarchery.littournaments.enums.RedisMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RedisHandler {

	public static final String CHANNEL_NAME = "littournaments";
	private static final String SERVER_LIST_KEY = "littournaments:servers";
	private static final String SERVER_KEY_PREFIX = "littournaments:server:";
	private static final int KEEPALIVE_INTERVAL_SECONDS = 30;
	private static final int KEEPALIVE_EXPIRY_SECONDS = 60;

	private static final Logger logger = LitTournaments.getLitLibs().getLogger();

	private static RedisHandler instance;
	private boolean isEnabled;

	private Jedis publisherJedis;
	private Jedis subscriberJedis;
	private Jedis storageJedis;
	private JedisPubSub subscriber;

	private BukkitTask keepAliveTask;
	private BukkitTask cleanupTask;

	private final UUID serverIdentifier = UUID.randomUUID();

	public static RedisHandler getInstance() {
		if (instance == null) instance = new RedisHandler();
		return instance;
	}

	private RedisHandler() { }

	public void initialize() {
		FileConfiguration config = LitTournaments.getInstance().getConfig();

		String enabledString = config.getString("Redis.enabled", "false");
		this.isEnabled = Boolean.parseBoolean(enabledString);

		if (!isEnabled) {
			logger.log("Redis is disabled in config.yml; skipping.");
			return;
		}

		String host = config.getString("Redis.host", "localhost");
		int port = config.getInt("Redis.port", 6379);
		String password = config.getString("Redis.password", "");

		try {
			publisherJedis = new Jedis(host, port);
			if (!password.isEmpty()) {
				publisherJedis.auth(password);
			}

			subscriberJedis = new Jedis(host, port);
			if (!password.isEmpty()) {
				subscriberJedis.auth(password);
			}

			storageJedis = new Jedis(host, port);
			if (!password.isEmpty()) {
				storageJedis.auth(password);
			}

			publisherJedis.ping();
			logger.log("Successfully connected to Redis for PubSub and Storage!");

			new Thread(() -> setUpListenEvent(LitTournaments.getInstance())).start();
			setUpKeepAlive();

			logger.log("Redis PubSub listener and keepalive system are set up and ready.");
			logger.log("Server UUID: " + serverIdentifier);

		} catch (Exception e) {
			LitTournaments.getInstance().getLogger().severe("Failed to connect to Redis: " + e.getMessage());
		}
	}

	private void setUpListenEvent(LitTournaments pluginInstance) {

		subscriber = new JedisPubSub() {
			@Override
			public void onMessage(String channel, String message) {
				String[] parts = message.split(" ");
				if (parts.length < 2) return;

				RedisMessage action = RedisMessage.valueOf(parts[0]);

				List<String> args = List.of(parts).subList(1, parts.length);

				Bukkit.getScheduler().runTask(pluginInstance, () -> {
					RedisUpdateEvent event = new RedisUpdateEvent(action, args);
					Bukkit.getServer().getPluginManager().callEvent(event);
				});
			}
		};

		subscriberJedis.subscribe(subscriber, CHANNEL_NAME);

	}

	private void setUpKeepAlive() {
		addServerToList();
		sendKeepAlive();

		keepAliveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
				LitTournaments.getInstance(),
				this::sendKeepAlive,
				0L,
				KEEPALIVE_INTERVAL_SECONDS * 20L
		);

		cleanupTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
				LitTournaments.getInstance(),
				this::cleanupExpiredServers,
				KEEPALIVE_INTERVAL_SECONDS * 20L,
				KEEPALIVE_INTERVAL_SECONDS * 20L
		);

		logger.log("Keepalive system started for server: " + serverIdentifier);
	}

	private void addServerToList() {
		if (storageJedis == null) return;

		storageJedis.sadd(SERVER_LIST_KEY, serverIdentifier.toString());
	}

	private void removeServerFromList() {
		if (storageJedis == null) return;

		storageJedis.srem(SERVER_LIST_KEY, serverIdentifier.toString());
		storageJedis.del(SERVER_KEY_PREFIX + serverIdentifier.toString());

		logger.log("Server removed from active servers list: " + serverIdentifier);
	}

	private void sendKeepAlive() {
		if (storageJedis == null) return;

		try {
			String serverKey = SERVER_KEY_PREFIX + serverIdentifier.toString();
			long timestamp = System.currentTimeMillis();
			storageJedis.setex(serverKey, KEEPALIVE_EXPIRY_SECONDS, String.valueOf(timestamp));

			storageJedis.sadd(SERVER_LIST_KEY, serverIdentifier.toString());

		} catch (Exception e) {
			logger.warn("Failed to send keepalive: " + e.getMessage());
		}
	}

	public Set<String> getActiveServers() {
		if (storageJedis == null) {
			throw new IllegalStateException("Redis not initialized! Call initialize() first.");
		}

		Set<String> allServers = storageJedis.smembers(SERVER_LIST_KEY);

		return allServers.stream()
				.filter(serverId -> {
					String serverKey = SERVER_KEY_PREFIX + serverId;
					return storageJedis.exists(serverKey);
				})
				.collect(Collectors.toSet());
	}

	public boolean shouldGiveOutRewards() {
		Set<String> activeServers = getActiveServers();

		UUID lowestServerUUID = null;

		for(String activeServer : activeServers) {
			UUID serverUUID = UUID.fromString(activeServer);

			if (lowestServerUUID == null || serverUUID.compareTo(lowestServerUUID) < 0) {
				lowestServerUUID = serverUUID;
			}
		}

		if (lowestServerUUID == null) {
			logger.warn("No active servers found, cannot determine if rewards should be given out.");
			return false;
		}

		return serverIdentifier.equals(lowestServerUUID);
	}

	public void cleanupExpiredServers() {
		if (storageJedis == null) return;

		Set<String> allServers = storageJedis.smembers(SERVER_LIST_KEY);

		for (String serverId : allServers) {
			String serverKey = SERVER_KEY_PREFIX + serverId;
			if (!storageJedis.exists(serverKey)) {
				storageJedis.srem(SERVER_LIST_KEY, serverId);
				logger.log("Cleaned up expired server: " + serverId);
			}
		}
	}

	public UUID getServerIdentifier() {
		return serverIdentifier;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	private void publish(String message) {
		if (publisherJedis == null) {
			throw new IllegalStateException("Redis not initialized! Call initialize() first.");
		}
		publisherJedis.publish(RedisHandler.CHANNEL_NAME, message);
	}

	public void sendUpdate(RedisMessage message, String... args) {
		StringBuilder sb = new StringBuilder(message.name());
		for (String arg : args) {
			sb.append(" ").append(arg);
		}
		publish(sb.toString());
		LitTournaments.getInstance().getLogger().info("Published Redis message: " + sb.toString());
	}

	public void shutdown() {
		if (keepAliveTask != null && !keepAliveTask.isCancelled()) {
			keepAliveTask.cancel();
		}

		if (cleanupTask != null && !cleanupTask.isCancelled()) {
			cleanupTask.cancel();
		}

		removeServerFromList();

		if (subscriber != null && subscriber.isSubscribed()) {
			subscriber.unsubscribe();
		}

		if (publisherJedis != null && publisherJedis.isConnected()) {
			publisherJedis.close();
		}
		if (subscriberJedis != null && subscriberJedis.isConnected()) {
			subscriberJedis.close();
		}
		if (storageJedis != null && storageJedis.isConnected()) {
			storageJedis.close();
		}

		logger.log("Redis connections closed and server removed from active list.");
	}
}