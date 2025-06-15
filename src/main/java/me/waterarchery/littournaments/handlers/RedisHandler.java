package me.waterarchery.littournaments.handlers;

import me.waterarchery.litlibs.logger.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
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

	private static final int MAX_TOTAL_CONNECTIONS = 20;
	private static final int MAX_IDLE_CONNECTIONS = 10;
	private static final int MIN_IDLE_CONNECTIONS = 2;
	private static final int CONNECTION_TIMEOUT_MS = 3000;

	private static final int SUBSCRIBER_MAX_TOTAL = 5;
	private static final int SUBSCRIBER_MAX_IDLE = 2;
	private static final int SUBSCRIBER_MIN_IDLE = 1;

	private static final Logger logger = LitTournaments.getLitLibs().getLogger();

	private static RedisHandler instance;
	private boolean isEnabled;

	private JedisPool mainPool;
	private JedisPool subscriberPool;
	private JedisPubSub subscriber;
	private Thread subscriberThread;

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
			mainPool = createMainPool(host, port, password);
			subscriberPool = createSubscriberPool(host, port, password);

			try (Jedis jedis = mainPool.getResource()) {
				jedis.ping();
				logger.log("Successfully connected to Redis with connection pools!");
			}

			setUpSubscriber(LitTournaments.getInstance());

			setUpKeepAlive();

			logger.log("Redis connection pools, PubSub listener, and keepalive system are set up and ready.");

		} catch (Exception e) {
			LitTournaments.getInstance().getLogger().severe("Failed to connect to Redis: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private JedisPool createMainPool(String host, int port, String password) {
		JedisPoolConfig poolConfig = new JedisPoolConfig();

		poolConfig.setMaxTotal(MAX_TOTAL_CONNECTIONS);
		poolConfig.setMaxIdle(MAX_IDLE_CONNECTIONS);
		poolConfig.setMinIdle(MIN_IDLE_CONNECTIONS);

		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setTestWhileIdle(true);

		poolConfig.setBlockWhenExhausted(true);

		if (password.isEmpty()) {
			return new JedisPool(poolConfig, host, port, CONNECTION_TIMEOUT_MS);
		} else {
			return new JedisPool(poolConfig, host, port, CONNECTION_TIMEOUT_MS, password);
		}
	}

	private JedisPool createSubscriberPool(String host, int port, String password) {
		JedisPoolConfig poolConfig = new JedisPoolConfig();

		poolConfig.setMaxTotal(SUBSCRIBER_MAX_TOTAL);
		poolConfig.setMaxIdle(SUBSCRIBER_MAX_IDLE);
		poolConfig.setMinIdle(SUBSCRIBER_MIN_IDLE);

		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setTestWhileIdle(true);

		poolConfig.setBlockWhenExhausted(true);

		if (password.isEmpty()) {
			return new JedisPool(poolConfig, host, port, CONNECTION_TIMEOUT_MS);
		} else {
			return new JedisPool(poolConfig, host, port, CONNECTION_TIMEOUT_MS, password);
		}
	}

	private void setUpSubscriber(LitTournaments pluginInstance) {
		subscriber = new JedisPubSub() {
			@Override
			public void onMessage(String channel, String message) {
				String[] parts = message.split(" ");
				if (parts.length < 2) return;

				try {
					RedisMessage action = RedisMessage.valueOf(parts[0]);
					List<String> args = List.of(parts).subList(1, parts.length);

					Bukkit.getScheduler().runTask(pluginInstance, () -> {
						RedisUpdateEvent event = new RedisUpdateEvent(action, args);
						Bukkit.getServer().getPluginManager().callEvent(event);
					});
				} catch (IllegalArgumentException e) {
					logger.warn("Received unknown Redis message action: " + parts[0]);
				}
			}

			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
				logger.log("Successfully subscribed to Redis channel: " + channel);
			}

			@Override
			public void onUnsubscribe(String channel, int subscribedChannels) {
				logger.log("Unsubscribed from Redis channel: " + channel);
			}
		};

		subscriberThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try (Jedis jedis = subscriberPool.getResource()) {
					logger.log("Starting Redis subscription listener...");
					jedis.subscribe(subscriber, CHANNEL_NAME);
				} catch (Exception e) {
					if (!Thread.currentThread().isInterrupted()) {
						logger.warn("Redis subscription connection lost, retrying in 5 seconds: " + e.getMessage());
						try {
							Thread.sleep(5000);
						} catch (InterruptedException ie) {
							Thread.currentThread().interrupt();
							break;
						}
					}
				}
			}
		}, "Redis-Subscriber-Thread");

		subscriberThread.setDaemon(true);
		subscriberThread.start();
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
	}

	private void addServerToList() {
		if (mainPool == null) return;

		try (Jedis jedis = mainPool.getResource()) {
			jedis.sadd(SERVER_LIST_KEY, serverIdentifier.toString());
		} catch (Exception e) {
			logger.warn("Failed to add server to list: " + e.getMessage());
		}
	}

	private void removeServerFromList() {
		if (mainPool == null) return;

		try (Jedis jedis = mainPool.getResource()) {
			jedis.srem(SERVER_LIST_KEY, serverIdentifier.toString());
			jedis.del(SERVER_KEY_PREFIX + serverIdentifier.toString());
			logger.log("Server removed from active servers list: " + serverIdentifier);
		} catch (Exception e) {
			logger.warn("Failed to remove server from list: " + e.getMessage());
		}
	}

	private void sendKeepAlive() {
		if (mainPool == null) return;

		try (Jedis jedis = mainPool.getResource()) {
			String serverKey = SERVER_KEY_PREFIX + serverIdentifier.toString();
			long timestamp = System.currentTimeMillis();
			jedis.setex(serverKey, KEEPALIVE_EXPIRY_SECONDS, String.valueOf(timestamp));
			jedis.sadd(SERVER_LIST_KEY, serverIdentifier.toString());
		} catch (Exception e) {
			logger.warn("Failed to send keepalive: " + e.getMessage());
		}
	}

	public Set<String> getActiveServers() {
		if (mainPool == null) {
			throw new IllegalStateException("Redis not initialized! Call initialize() first.");
		}

		try (Jedis jedis = mainPool.getResource()) {
			Set<String> allServers = jedis.smembers(SERVER_LIST_KEY);

			return allServers.stream()
					.filter(serverId -> {
						String serverKey = SERVER_KEY_PREFIX + serverId;
						return jedis.exists(serverKey);
					})
					.collect(Collectors.toSet());
		}
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
		if (mainPool == null) return;

		try (Jedis jedis = mainPool.getResource()) {
			Set<String> allServers = jedis.smembers(SERVER_LIST_KEY);

			for (String serverId : allServers) {
				String serverKey = SERVER_KEY_PREFIX + serverId;
				if (!jedis.exists(serverKey)) {
					jedis.srem(SERVER_LIST_KEY, serverId);
					logger.log("Cleaned up expired server: " + serverId);
				}
			}
		} catch (Exception e) {
			logger.warn("Failed to cleanup expired servers: " + e.getMessage());
		}
	}

	public UUID getServerIdentifier() {
		return serverIdentifier;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	private void publish(String message) {
		if (mainPool == null) {
			throw new IllegalStateException("Redis not initialized! Call initialize() first.");
		}

		try (Jedis jedis = mainPool.getResource()) {
			jedis.publish(CHANNEL_NAME, message);
		}
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
		logger.log("Shutting down Redis handler...");

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

		if (subscriberThread != null && subscriberThread.isAlive()) {
			subscriberThread.interrupt();
			try {
				subscriberThread.join(5000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		if (mainPool != null && !mainPool.isClosed()) {
			mainPool.close();
		}

		if (subscriberPool != null && !subscriberPool.isClosed()) {
			subscriberPool.close();
		}

		logger.log("Redis connections closed and server removed from active list.");
	}
}