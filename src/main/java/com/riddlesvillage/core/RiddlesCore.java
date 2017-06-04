/*
 * RiddlesCore
 */

package com.riddlesvillage.core;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.riddlesvillage.core.database.Database;
import com.riddlesvillage.core.internal.command.*;
import com.riddlesvillage.core.internal.config.DatabaseConfig;
import com.riddlesvillage.core.internal.listener.player.PlayerListeners;
import com.riddlesvillage.core.net.Messenger;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.CorePlayerManager;
import com.riddlesvillage.core.service.timer.Timer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.riddlesvillage.core.player.CorePlayer.PLAYER_MANAGER;

public final class RiddlesCore extends JavaPlugin {

	private static final CoreSettings settings = new CoreSettings();
	private static RiddlesCore instance;
	private final Messenger messenger = new Messenger(this, true, 60);
	private final Database database = Database.getInstance();

	@Override
	public void onEnable() {
		try {
			// Record the time taken to load RiddlesCore
			final Timer t = new Timer().start();

			instance = this;

			settings.initClasses(
					// Load configuration and language files
					"com.riddlesvillage.core.internal.config.MessagesConfig",
					"com.riddlesvillage.core.internal.config.MainConfig",
					"com.riddlesvillage.core.internal.config.SpawnsConfig",
					"com.riddlesvillage.core.internal.config.DatabaseConfig",

					// Start Boss Bar timer to ensure no errors occur onDisable() if noone joins
					"com.riddlesvillage.core.util.BossBar"
			);

			settings.addLocale(CoreSettings.DEFAULT_LOCALE);

			// Internal event listeners
			settings.registerListeners(this, PlayerListeners.get());

			// Register default RiddlesCore commands
			settings.registerCommands(this, new ImmutableMap.Builder<String, CommandExecutor>()
							.put("addspawn",	new AddSpawnCommand())
							.put("clearchat",	new ClearChatCommand())
							.put("coins",		new CoinsCommand())
							.put("debug",		new DebugCommand())
							.put("god",			new GodCommand())
							.put("premium",		new PremiumCommand())
							.put("rank",		new RankCommand())
							.put("stats",		new StatsCommand())
							.put("tokens",		new TokensCommand())
							.put("tpspawn",		new TPSpawnCommand())
							.put("vanish",		new VanishCommand())
							.build()
			);

			getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

			// Initialize database connection and setup management
			database.init(DatabaseConfig.getCredentials());

			PluginDescriptionFile desc = getDescription();

			log("~&3=========== &eRiddlesCore&3 ===========");
			log("~&3=> Version: &e%s", desc.getVersion());
			log("~&3=> Authors: &e%s", desc.getAuthors());
			log("~&3=> Loaded in &e%sms", t.forceStop().getTime(TimeUnit.MILLISECONDS));
			log("~&3===================================");
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.shutdown();
		}
	}

	@Override
	public void onDisable() {


		// Kick all players
		for (CorePlayer player : PLAYER_MANAGER) {
			Player bukkitPlayer = player.getPlayer();
			bukkitPlayer.kickPlayer(settings.get("restart"));
			Bukkit.getPluginManager().callEvent(new PlayerQuitEvent(bukkitPlayer, null));
		}

		// Close the database connection after 5 milliseconds for all tasks to finish first
		ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
		ex.schedule(database::close, 5, TimeUnit.MILLISECONDS);
		ex.shutdown();
	}

	public static Optional<String> log(String path,
									   Object... components) {
		return Messaging.log(settings.get(path), components);
	}

	public static Optional<String> log(String path,
									   String[] keys,
									   Object... vals) {
		return log(Messaging.constructReplacements(settings.get(path), keys, vals));
	}

	public static Optional<String> log(String path,
									   Map<String, Object> replacements) {
		return log(Messaging.constructReplacements(settings.get(path), replacements));
	}

	public static boolean logIf(boolean check,
								String path,
								Object... components) {
		return Messaging.logIf(check, settings.get(path), components);
	}

	public static boolean logIf(boolean check,
								String path,
								String[] keys,
								Object... vals) {
		return logIf(check, Messaging.constructReplacements(settings.get(path), keys, vals));
	}

	public static boolean logIf(boolean check,
								String path,
								Map<String, Object> replacements) {
		return logIf(check, Messaging.constructReplacements(settings.get(path), replacements));
	}

	public static void broadcast(String message,
								 Object... components) {
		for (CorePlayer player : CorePlayerManager.getInstance()) {
			player.sendMessage(message, components);
		}

		log(message, components);
	}

	public static void broadcast(String path,
								 String[] keys, Object... vals) {
		broadcast(Messaging.constructReplacements(settings.get(path), keys, vals));
	}

	public static void broadcast(String path,
								 Map<String, Object> replacements) {
		broadcast(Messaging.constructReplacements(settings.get(path), replacements));
	}

	public static boolean sendServerMessage(String channel,
											byte[] data) {
		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		if (player == null) return false;

		player.sendPluginMessage(instance, channel, data);
		return true;
	}

	public static CoreSettings getSettings() {
		return settings;
	}

	public static Messenger getMessenger() {
		return instance.messenger;
	}

	public static RiddlesCore getInstance() {
		return instance;
	}
}