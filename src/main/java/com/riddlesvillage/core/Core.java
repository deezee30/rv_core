/*
 * RiddlesCore
 */

package com.riddlesvillage.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.riddlesvillage.core.chat.ChatMessages;
import com.riddlesvillage.core.database.Database;
import com.riddlesvillage.core.database.data.Credentials;
import com.riddlesvillage.core.internal.command.*;
import com.riddlesvillage.core.internal.config.DatabaseConfig;
import com.riddlesvillage.core.internal.config.MainConfig;
import com.riddlesvillage.core.internal.listener.player.PlayerListeners;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.manager.CorePlayerManager;
import com.riddlesvillage.core.service.timer.Timer;
import com.riddlesvillage.core.world.CoreWorldManager;
import com.riddlesvillage.core.world.Vector3D;
import com.riddlesvillage.core.world.region.Region;
import com.riddlesvillage.core.world.region.Regions;
import com.riddlesvillage.core.world.region.flag.Flag;
import com.riddlesvillage.core.world.region.flag.FlagMap;
import com.riddlesvillage.core.world.schematic.Schematics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

import static com.riddlesvillage.core.player.CorePlayer.PLAYER_MANAGER;

public final class Core extends JavaPlugin {

    private static final CoreSettings settings = new CoreSettings();
    private static Core instance;
    private final Database database = Database.get();
    private final Timer loadTimer = new Timer();
    private final Logger logger = new Logger();

    @Override
    public void onLoad() {
        instance = this;

        // start and warm up the default fork pool executor
        try {
            CompletableFuture.supplyAsync(() -> null).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        try {
            // Record the time taken to load RiddlesCore
            loadTimer.start();

            settings.initClasses(
                    // Load configuration and language files
                    "com.riddlesvillage.core.internal.config.MessagesConfig",
                    "com.riddlesvillage.core.internal.config.MainConfig",
                    "com.riddlesvillage.core.internal.config.SpawnsConfig",
                    "com.riddlesvillage.core.internal.config.DatabaseConfig"
            );

            // Set up default language for players
            settings.addLocale(MainConfig.getDefaultLocale());

            // Internal event listeners
            settings.registerListeners(this, PlayerListeners.get());
            settings.registerListeners(this, CoreWorldManager.getInstance());

            // Register default RiddlesCore commands
            settings.registerCommands(this, new ImmutableMap.Builder<String, CommandExecutor>()
                    .put("addspawn",    new AddSpawnCommand())
                    .put("clearchat",   new ClearChatCommand())
                    .put("coins",       new CoinsCommand())
                    .put("debug",       new DebugCommand())
                    .put("iphistory",   new IpHistoryCommand())
                    .put("namehistory", new NameHistoryCommand())
                    .put("god",         new GodCommand())
                    .put("premium",     new PremiumCommand())
                    .put("rank",        new RankCommand())
                    .put("teleport",    new TeleportCommand())
                    .put("stats",       new StatsCommand())
                    .put("tokens",      new TokensCommand())
                    .put("tpspawn",     new TPSpawnCommand())
                    .put("vanish",      new VanishCommand())
                    .build()
            );

            // Allow commands when commands are disabled
            settings.addAllowedCommands(MainConfig.getAllowedCommands());

            // Register default chat block filters
            settings.getChatFilters().registerDefaults();

            ConfigurationSerialization.registerClass(Vector3D.class);
            ConfigurationSerialization.registerClass(FlagMap.class);
            ConfigurationSerialization.registerClass(Region.class);

            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

            Flag.init();
            // init region manager and load default regions
            Regions.getManager().init();
            // init schematics
            Schematics.init(false);

            // Initialize database connection and setup management if credentials are set
            Credentials cr = DatabaseConfig.getCredentials();
            if (!cr.isSet()) {
                log("~&4Default credentials have been generated");
                log("~&4in file 'plugins/RiddlesCore/database.yml'");
                log("~&4Please change them to actual credentials...");
                Bukkit.shutdown();
                return;
            }

            database.init(logger, cr);

            PluginDescriptionFile desc = getDescription();

            log("~&3=========== &eRiddlesCore&3 ===========");
            log("~&3=> Version: &e%s", desc.getVersion());
            log("~&3=> Authors: &e%s", desc.getAuthors());
            log("~&3=> Loaded in &e%sms", loadTimer.forceStop().getTime(TimeUnit.MILLISECONDS));
            log("~&3===================================");
        } catch (Exception e) {
            log("~&4A startup error has occurred - shutting down");
            e.printStackTrace();
            Bukkit.shutdown();
        }
    }

    @Override
    public void onDisable() {
        // Paste all messages in this session
        ChatMessages.getInstance().pasteChatMessages();

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

    @Override
    public String toString() {
        return getName();
    }

    public static Optional<String> log(final String path,
                                       final Object... components) {
        return instance.logger.log(settings.get(path), components);
    }

    public static Optional<String> log(final String path,
                                       final String[] keys,
                                       final Object... vals) {
        return log(Logger.constructReplacements(settings.get(path), keys, vals));
    }

    public static Optional<String> log(final String path,
                                       final Map<String, Object> replacements) {
        return log(Logger.constructReplacements(settings.get(path), replacements));
    }

    public static boolean logIf(final boolean check,
                                final String path,
                                final Object... components) {
        return instance.logger.logIf(check, settings.get(path), components);
    }

    public static boolean logIf(final boolean check,
                                final String path,
                                final String[] keys,
                                final Object... vals) {
        return logIf(check, Logger.constructReplacements(settings.get(path), keys, vals));
    }

    public static boolean logIf(final boolean check,
                                final String path,
                                final Map<String, Object> replacements) {
        return logIf(check, Logger.constructReplacements(settings.get(path), replacements));
    }

    public static Optional<String> debug(final String string,
                                  final Object... components) {
        return instance.logger.debug(string, components);
    }

    public static boolean debugIf(final boolean check,
                                    final String string,
                                    final Object... components) {
        return instance.logger.debugIf(check, string, components);
    }

    public static void broadcast(final String message,
                                 final Object... components) {
        for (CorePlayer player : CorePlayerManager.getInstance()) {
            player.sendMessage(message, components);
        }

        log(message, components);
    }

    public static void broadcast(final String path,
                                 final String[] keys,
                                 final Object... vals) {
        broadcast(Logger.constructReplacements(settings.get(path), keys, vals));
    }

    public static void broadcast(final String path,
                                 final Map<String, Object> replacements) {
        broadcast(Logger.constructReplacements(settings.get(path), replacements));
    }

    public static Logger getCoreLogger() {
        return instance.logger;
    }

    public static boolean sendServerMessage(final String channel,
                                            final byte[] data) {
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player == null) return false;

        player.sendPluginMessage(instance, channel, data);
        return true;
    }

    public static CoreSettings getSettings() {
        return settings;
    }

    public static Core get() {
        return instance;
    }
}