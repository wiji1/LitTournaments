package me.waterarchery.littournaments.handlers;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import me.waterarchery.littournaments.LitTournaments;
import me.waterarchery.littournaments.api.events.RedisUpdateEvent;
import me.waterarchery.littournaments.enums.RedisMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class RedisHandler {

	public static final String CHANNEL_NAME = "littournaments";

	private static RedisHandler instance;
	private Jedis publisherJedis;
	private Jedis subscriberJedis;
	private JedisPubSub subscriber;

	public static RedisHandler getInstance() {
		if (instance == null) instance = new RedisHandler();
		return instance;
	}

	private RedisHandler() { }

	public void initialize() {
		FileConfiguration config = LitTournaments.getInstance().getConfig();

		String enabledString = config.getString("Redis.enabled", "false");
		boolean enabled = Boolean.parseBoolean(enabledString);

		if (!enabled) {
			LitTournaments.getInstance().getLogger().info("Redis is disabled in config.yml; skipping.");
			return;
		}

		// Get Redis configuration from config.yml
		String host = config.getString("Redis.host", "localhost");
		int port = config.getInt("Redis.port", 6379);
		String password = config.getString("Redis.password", "");

		try {
			// Initialize publisher Jedis connection
			publisherJedis = new Jedis(host, port);
			if (!password.isEmpty()) {
				publisherJedis.auth(password);
			}

			// Initialize subscriber Jedis connection
			subscriberJedis = new Jedis(host, port);
			if (!password.isEmpty()) {
				subscriberJedis.auth(password);
			}

			// Test connection
			publisherJedis.ping();
			LitTournaments.getInstance().getLogger().info("Successfully connected to Redis for PubSub!");

			new Thread(() -> setUpListenEvent(LitTournaments.getInstance())).start();

			LitTournaments.getInstance().getLogger().info("Redis PubSub listener is set up and ready.");

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
		if (subscriber.isSubscribed()) subscriber.unsubscribe();

		if (publisherJedis != null && publisherJedis.isConnected() ) {
			publisherJedis.close();
		}
		if (subscriberJedis != null && subscriberJedis.isConnected()) {
			subscriberJedis.close();
		}
		LitTournaments.getInstance().getLogger().info("Redis PubSub connections closed.");
	}
}