/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.Atomics;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.riddlesvillage.core.CoreSettings;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.database.Database;
import com.riddlesvillage.core.database.DatabaseAPI;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.database.data.DataOperator;
import com.riddlesvillage.core.internal.config.MainConfig;
import com.riddlesvillage.core.player.event.CorePlayerPostLoadEvent;
import com.riddlesvillage.core.player.profile.AbstractCoreProfile;
import com.riddlesvillage.core.player.profile.CoreProfile;
import com.riddlesvillage.core.scoreboard.ScoreboardFactory;
import com.riddlesvillage.core.service.timer.Timer;
import com.riddlesvillage.core.util.Firework;
import com.riddlesvillage.core.util.inventory.InventoryManager;
import com.riddlesvillage.core.util.inventory.item.CoreItemStackList;
import com.riddlesvillage.core.util.inventory.item.IndexedItem;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An online-only version of any user
 * ({@link CoreProfile}) that is online.
 *
 * <p>This class can only have a single instance per online
 * player, per server.  Online player instances are stored
 * in {@link CorePlayerManager}.</p>
 */
public class CorePlayer extends AbstractCoreProfile implements ScoreboardHolder {

	private static final RiddlesCore INSTANCE = RiddlesCore.getInstance();
	private static final CoreSettings SETTINGS = RiddlesCore.getSettings();

	private transient final Player player;

	private transient InventoryManager
			invManager 		= null;

	private transient ScoreboardHolder
			sbHolder 		= this;

	private transient String
			locale 			= SETTINGS.getLocaleOrDefault(MainConfig.getDefaultLocale());

	private transient boolean
			vanished 		= false,
			cmdBlocked 		= false,
			damageable 		= true,
			constructable 	= true,
			movable			= true,
			canGetHungry	= true;

	private transient double
			coinMultiplier	= 1.0d;

	CorePlayer(Player player, String assumedHostName) {
		super(player.getUniqueId(), player.getName());
		this.player = player;

		final AtomicReference<String> hostName
				= Atomics.newReference(assumedHostName == null ? "127.0.0.1" : assumedHostName);

		// Check if player exists in database collection
		boolean newcomer = !hasPlayed();

		if (newcomer) {
			// If not, generate a new row for this player with default values
			Map<String, Object> doc = Maps.newHashMap();

			DataInfo.UUID.append(doc, getUuid());
			DataInfo.NAME.append(doc, getName());
			DataInfo.NAME_HISTORY.append(doc);
			DataInfo.IP_HISTORY.append(doc);
			DataInfo.FIRST_LOGIN.append(doc, System.currentTimeMillis() / 1000);
			DataInfo.LAST_LOGIN.append(doc, System.currentTimeMillis() / 1000);
			DataInfo.LAST_LOGOUT.append(doc);
			DataInfo.PLAYING.append(doc);
			DataInfo.COINS.append(doc);
			DataInfo.TOKENS.append(doc);
			DataInfo.RANK.append(doc);
			DataInfo.PREMIUM.append(doc);
			DataInfo.LOCALE.append(doc);

			DatabaseAPI.insertNew(
					Database.getMainCollection(), doc,
					(result, t1) -> Messaging.debug("New player %s successfully created", getName())
			);
		} else {
			// Player's name and stats may have changed since his last query - Remove him from cache
			OfflineCorePlayer.removeFromCache(getUuid());

			// Update player locale from downloaded document
			locale = downloadedDoc.getString("locale");

			List<UpdateOneModel<Document>> operations = Lists.newArrayList();
			Bson searchQuery = Filters.eq("uuid", getUuid());

			// update name
			operations.add(new UpdateOneModel<>(searchQuery, new Document(
					DataOperator.$SET.getOperator(),
					new Document(DataInfo.NAME.getStat(), getName()))));

			// update name history
			operations.add(new UpdateOneModel<>(searchQuery, new Document(
					DataOperator.$SET.getOperator(),
					new Document(DataInfo.NAME_HISTORY.getStat(), getNameHistory()))));

			// update IP
			operations.add(new UpdateOneModel<>(searchQuery, new Document(
					DataOperator.$SET.getOperator(),
					new Document(DataInfo.IP_HISTORY.getStat(), getIpHistory()))));

			// update playing
			operations.add(new UpdateOneModel<>(searchQuery, new Document(
					DataOperator.$SET.getOperator(),
					new Document(DataInfo.PLAYING.getStat(), true))));

			// update last login time
			operations.add(new UpdateOneModel<>(searchQuery, new Document(
					DataOperator.$SET.getOperator(),
					new Document(DataInfo.LAST_LOGIN.getStat(), System.currentTimeMillis() / 1000))));

			// submit bulk update and await for result
			DatabaseAPI.bulkUpdate(operations, bulkWriteResult -> RiddlesCore.logIf(
					!bulkWriteResult.wasAcknowledged(),
					"Bulk update failed for %s (login)",
					getName()
			));
		}

		invManager = new InventoryManager(this);

		Messaging.debug("%s's default language is %s", getName(), WordUtils.capitalize(locale));
		timer.forceStop();

		// Delay task until the CraftPlayer instance has been fully loaded
		new BukkitRunnable() {

			@Override
			public void run() {

				// Check how long it takes to load the player in other events via RiddlesCore
				final Timer eventTimer = new Timer().start();
				eventTimer.onFinishExecute(() -> Messaging.debug(
						"'%s' was loaded in other plugins in %sms",
						CorePlayer.this,
						eventTimer.getTime(TimeUnit.MILLISECONDS)
				));

				/*
				 * Call event for other plugins using RiddlesCore
				 * to load CorePlayer instances after this instance
				 * loads. Make sure player actually logged in and
				 * is online before calling all other events.
				 */
				if (player.isOnline()) {
					player.setDisplayName(getRank().getColor() + player.getName());

					INSTANCE.getServer().getPluginManager().callEvent(
							new CorePlayerPostLoadEvent(CorePlayer.this, newcomer));

					// Give player the default items
					giveLoginItems();
				} else {
					// Player has been revoked access to server
					destroy();
				}

				eventTimer.forceStop();
			}
		}.runTask(INSTANCE);
	}

	// == Native ====================================================== //

	/**
	 * @return	A new copy of {@code Location} containing the position of this entity.
	 * @see		Player#getLocation();
	 */
	public Location getLocation() {
		return player.getLocation();
	}

	/**
	 * @return The delegated online {@code Player} instance.
	 */
	public Player getPlayer() {
		return player;
	}

	@Override
	public Player getBukkitPlayer() {
		return player;
	}

	@Override
	public CorePlayer toCorePlayer() {
		return this;
	}

	@Override
	public String getIp() {
		return player.getAddress().getHostName();
	}

	/**
	 * @return	The player's custom display name or {@code
	 * 			ChatColor.YELLOW + getName()} if he has none.
	 * @see		Player#getDisplayName()
	 */
	public String getDisplayName() {
		return player.getDisplayName().equals(getName()) ?
				ChatColor.YELLOW + getName()
				: player.getDisplayName();
	}

	/**
	 * Sends multiple localized messages from the locales via the provided
	 * paths and replacements.
	 *
	 * <p>The returned messages from the paths are always formattable
	 * via {@link java.util.Formatter}.</p>
	 *
	 * <p>If the message returned from the path provided is equal
	 * to the path provided, the message returned is taken as an
	 * already defined message.</p>
	 *
	 * <p>If the message returned equals {@link Messaging#getNoPrefixChar()}
	 * or if the path returned is still remained as a path, then the
	 * message is blocked.</p>
	 *
	 * @param	paths
	 * 			Multiple paths for the messages.
	 * @param	components
	 * 			The replacements for variables.
	 * @throws	NullPointerException If any component is {@code null}.
	 * @throws	java.util.IllegalFormatException
	 *          If a format string contains an illegal syntax, a format
	 *          specifier that is incompatible with the given arguments,
	 *          insufficient arguments given the format string, or other
	 *          illegal conditions.  For specification of all possible
	 *          formatting errors, see the <a
	 *          href="../util/Formatter.html#detail">Details</a> section
	 *          of the formatter class specification.
	 * @see		CoreSettings#get(String, String)
	 * @see		java.util.Formatter
	 * @see		String#format(String, Object...)
	 * @see		#sendMessage(String, Object...)
	 * @see		#sendMessage(String, String[], Object...)
	 * @see		#sendMessage(String, Map)
	 */
	public void sendMessages(String[] paths, Object... components) {
		for (String path : paths) {
			sendMessage(path, components);
		}
	}

	/**
	 * Sends a localized message from the locales via the provided
	 * path and replacements.
	 *
	 * <p>The returned message from the path is always formattable
	 * via {@link java.util.Formatter}.</p>
	 *
	 * <p>If the message returned from the path provided is equal
	 * to the path provided, the message returned is taken as an
	 * already defined message.</p>
	 *
	 * <p>If the message returned equals {@link Messaging#getNoPrefixChar()}
	 * or if the path returned is still remained as a path, then the
	 * message is blocked.</p>
	 *
	 * @param	path
	 * 			The path for the message.
	 * @param	components
	 * 			The replacements for variables.
	 * @throws	NullPointerException
	 * 			If any component is {@code null}.
	 * @throws	java.util.IllegalFormatException
	 * 			If a format string contains an illegal syntax, a format
	 * 			specifier that is incompatible with the given arguments,
	 * 			insufficient arguments given the format string, or other
	 * 			illegal conditions.  For specification of all possible
	 * 			formatting errors, see the <a
	 * 			href="../util/Formatter.html#detail">Details</a> section
	 * 			of the formatter class specification.
	 * @see		CoreSettings#get(String, String)
	 * @see		java.util.Formatter
	 * @see		String#format(String, Object...)
	 * @see		#sendMessage(String, String[], Object...)
	 * @see		#sendMessage(String, Map)
	 */
	public void sendMessage(String path, Object... components) {
		if (!isOnline() || path == null) return;

		String message = SETTINGS.get(locale, path);

		/*
		 * Check if the path actually exists in the messages cache.
		 * If not, block the message if it's a path or send it if it's not.
		 */
		if (message.equals(path)) {
			if (!path.contains(" ") && !path.equals(String.valueOf(Messaging.getNoPrefixChar()))) {
				Messaging.debug("Blocking message '%s' from player %s (locale: %s)", path, this, locale);
				return;
			}
		}

		handleMessage(String.format(message, components));
	}

	/**
	 * Sends a localized message from the locales via the provided
	 * path and replacements.
	 *
	 * <p>If the message returned from the path provided is equal
	 * to the path provided, the message returned is taken as an
	 * already defined message.</p>
	 *
	 * <p>If the message returned equals {@link Messaging#getNoPrefixChar()}
	 * or if the path returned is still remained as a path, then the
	 * message is blocked.</p>
	 *
	 * <p>If the amount of {@param keys} does not equal to the amount of
	 * {@param values}, the minimum of the two will be the number of
	 * iterations (replacements) performed.</p>
	 *
	 * <p>If either any of the {@param keys} or {@param values} is {@code
	 * null} then that iteration is skipped.</p>
	 *
	 * <p>For example, invoking:
	 * <code>
	 * sendMessage(
	 *     "Hello, %NAME%!",        // Constructed message. Can also be a path.
	 *     new String[] {"%NAME%"}, // A String array of variables to search
	 *     "Steve"                  // An array of replacements to be used instead of variables in order.
	 * );
	 * </code>
	 * would build the message {@code Hello, Steve!}.</p>
	 *
	 * @param	path
	 * 			The path for the message. Can also be a plain message.
	 * @param	keys
	 * 			The String array of variables to search.
	 * @param	vals
	 * 			An array of replacements to be used instead of variables
	 * 			in order.
	 * @see     CoreSettings#get(String, String)
	 * @see		Messaging#constructReplacements(String, String[], Object...)
	 * @see		#sendMessage(String, Object...)
	 * @see		#sendMessage(String, Map)
	 */
	public void sendMessage(String path, String[] keys, Object... vals) {
		handleMessage(Messaging.constructReplacements(SETTINGS.get(locale, path), keys, vals));
	}

	/**
	 * Sends a localized message from the locales via the provided
	 * path and replacements.
	 *
	 * <p>If the message returned from the path provided is equal
	 * to the path provided, the message returned is taken as an
	 * already defined message.</p>
	 *
	 * <p>If the message returned equals {@link Messaging#getNoPrefixChar()}
	 * or if the path returned is still remained as a path, then the
	 * message is blocked.</p>
	 *
	 * <p>If either any of the keys or values in {@param replacements}
	 * is {@code null} then that iteration is skipped.</p>
	 *
	 * <p>For example, invoking:
	 * <code>
	 * sendMessage(
	 *     "Hello, %NAME%!",                          // Constructed message. Can also be a path.
	 *     new ImmutableMap.Builder<String, Object>() // A map of variables and their replacements.
	 *         .put("%NAME%", "Steve")
	 *         .build(),
	 * );
	 * </code>
	 * would build the message {@code Hello, Steve!}.</p>
	 *
	 * @param	path
	 * 			The path for the message. Can also be a plain message.
	 * @param	replacements
	 * 			A map of keys (variables to replace) and their values
	 * 			(replacements) that correspond to each other.
	 * @see		CoreSettings#get(String , String)
	 * @see		Messaging#constructReplacements(String, Map)
	 * @see		#sendMessage(String, Object...)
	 * @see		#sendMessage(String, String[], Object...)
	 */
	public void sendMessage(String path, Map<String, Object> replacements) {
		handleMessage(Messaging.constructReplacements(SETTINGS.get(locale, path), replacements));
	}

	private void handleMessage(String message) {

		/*
		 * Furnish the message further by applying color codes,
		 * components, prefix and player's set locale and send it to player.
		 */
		player.sendMessage(ChatColor.translateAlternateColorCodes('&',
				Messaging.prefix(message, SETTINGS.get(locale, "prefix"))
		));
	}

	// == User Data ====================================================== //

	/**
	 * @return Whether or not the player is allowed to build/break blocks.
	 */
	public boolean isConstructable() {
		return constructable;
	}

	/**
	 * Sets whether or not the player is allowed to build/break blocks.
	 *
	 * @param	constructable
	 * 			If the player is allowed to construct.
	 * @return	The value provided.
	 */
	public boolean setConstructable(boolean constructable) {
		return this.constructable = constructable;
	}

	/**
	 * @return Whether or not the player can get damaged.
	 */
	public boolean isDamageable() {
		return damageable;
	}

	/**
	 * Sets whether or not the player can get damaged.
	 *
	 * @param	damageable
	 * 			If the player can get damaged.
	 * @return	The value provided.
	 */
	public boolean setDamageable(boolean damageable) {
		return this.damageable = damageable;
	}

	/**
	 * @return Whether or not the player is allowed to move.
	 */
	public boolean isMovable() {
		return movable;
	}

	/**
	 * Sets whether or not the player is allowed to move.
	 *
	 * @param	movable
	 * 			If the player can move.
	 * @return	The value provided.
	 */
	public boolean setMovable(boolean movable) {
		return this.movable = movable;
	}

	/**
	 * @return	Whether or not the player is allowed to type
	 * 			commands that are not on the whitelist.
	 */
	public boolean isCommandsBlocked() {
		return cmdBlocked;
	}

	/**
	 * Sets whether or not the player is allowed to type
	 * commands that are not on the whitelist.
	 *
	 * @param	cmdBlocked
	 * 			If the player can type non-whitelisted commands.
	 * @return	The value provided.
	 */
	public boolean setCommandsBlocked(boolean cmdBlocked) {
		return this.cmdBlocked = cmdBlocked;
	}

	public boolean canGetHungry() {
		return canGetHungry;
	}

	public boolean setCanGetHungry(boolean canGetHungry) {
		return this.canGetHungry = canGetHungry;
	}

	/**
	 * @return The player's chosen locale. Default is specified in the config.
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * Updates the current locale of the player in cache and database.
	 *
	 * <p>The player's login items ({@link CoreSettings#getLoginItems()})
	 * are given to the player again, in case they have been localized.</p>
	 *
	 * @param	locale
	 * 			The new locale for the player.
	 * @return	The provided locale.
	 */
	public String setLocale(String locale) {
		this.locale = locale;
		DatabaseAPI.update(
				Database.getMainCollection(),
				getUuid(),
				DataOperator.$SET,
				DataInfo.LOCALE,
				locale,
				updateResult -> RiddlesCore.logIf(
						!updateResult.wasAcknowledged(),
						"%s's locale (%s) update was unacknowledged!",
						getName(),
						locale
				)
		);
		return locale;
	}

	@Override
	public double getCoinMultiplier() {
		return coinMultiplier;
	}

	@Override
	public void setCoinMultiplier(double factor) {
		if (factor < 0) return;
		coinMultiplier = factor;
	}

	// == Inventory State ====================================================== //

	/**
	 * @return	The current inventory state of the player
	 * 			in {@link CoreItemStackList} form.
	 * @see		CoreItemStackList
	 */
	public CoreItemStackList getItems() {
		return new CoreItemStackList(player.getInventory().getContents());
	}

	/**
	 * @return	The current armor state of the player
	 * 			in {@link CoreItemStackList} form.
	 * @see     CoreItemStackList
	 */
	public CoreItemStackList getArmor() {
		return new CoreItemStackList(player.getInventory().getArmorContents());
	}

	/**
	 * Gives the player standard default items registered
	 * via {@link CoreSettings#getLoginItems()}
	 */
	public void giveLoginItems() {
		reset();
		clear();

		PlayerInventory inv = player.getInventory();
		SETTINGS.getLoginItems().entrySet().stream()
				.filter(entry -> entry.getValue().test(this))
				.forEach(entry -> {
					IndexedItem item = entry.getKey();
					inv.setItem(item.getSlot(), item.buildWithLocaleSupport(locale));
				});
	}

	/**
	 * Clears the inventory and armor contents for the player.
	 */
	public void clear() {
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
	}

	/**
	 * Resets the player's state entirely.
	 *
	 * <p>
	 *     <ul>
	 *         <li>Experience is set to {@code 0F}.</li>
	 *         <li>Level is set to {@code 0}.</li>
	 *         <li>Compass target is set to the player's last location.</li>
	 *         <li>Food level is set to {@code 20}.</li>
	 *         <li>Health is set to {@link Player#getMaxHealth()}.</li>
	 *         <li>Saturation is set to {@code 100F}.</li>
	 *         <li>Exhaustion is set to {@code 0F}.</li>
	 *         <li>Fly speed is set to {@code 0.1F}.</li>
	 *         <li>Walk speed is set to {@code .2F}.</li>
	 *         <li>Fire ticks are set to {@code 0}.</li>
	 *         <li>All potion effects are removed.</li>
	 *         <li>Vanish is toggled off.</li>
	 *         <li>Invisibility watch is toggled off.</li>
	 *         <li>{@code GameMode} is set to {@code GameMode#SURVIVAL}.</li>
	 *     </ul>
	 * </p>
	 *
	 * <p>A delay of {@code 1L} is scheduled before removing the fire ticks.
	 * This is to prevent CraftBukkit incosistencies when CraftPlayer isn't
	 * fully respawned yet.</p>
	 */
	public void reset() {
		player.setExp(0F);
		player.setLevel(0);

		player.setCompassTarget(player.getLocation());

		player.setFoodLevel(20);
		player.setHealth(player.getMaxHealth());

		player.setSaturation(100F);
		player.setExhaustion(0F);

		player.setFlySpeed(.1F);
		player.setWalkSpeed(.2F);

		/*
		 * A delay is needed to remove fire ticks since this method
		 * can be called before CraftPlayer instance is fully respawned.
		 */
		Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, () -> player.setFireTicks(0), 1L);

		for (PotionEffect potion : player.getActivePotionEffects()) {
			player.removePotionEffect(potion.getType());
		}

		if (vanished) toggleVanish(true);

		// Inverse equals to check for player's gamemode being null (ie: when player logs in)
		if (!GameMode.SURVIVAL.equals(player.getGameMode())) {
			player.setGameMode(GameMode.SURVIVAL);
		}
	}

	// == Util ====================================================== //

	/**
	 * Spawns a random fireowork at the player's location.
	 *
	 * <p>Color, length and type are made completely random.</p>
	 */
	public void spawnFirework() {
		new Firework(player.getLocation()).spawn();
	}

	/**
	 * Enabled and disables other players from seeing the player.
	 *
	 * @param silent Whether or not to output a message to the player.
	 */
	public void toggleVanish(boolean silent) {
		if (vanished = !vanished) {
			for (CorePlayer player : PLAYER_MANAGER) {
				player.player.hidePlayer(this.player);
			}
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false));
			if (!silent) sendMessage("vanish.enable");
		} else {
			for (CorePlayer player : PLAYER_MANAGER) {
				player.player.showPlayer(this.player);
			}
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			if (!silent) sendMessage("vanish.disable");
		}
	}

	/**
	 * If BungeeCord is installed, attempts to send the player
	 * to the server provided.
	 *
	 * @param server The server to send the player to.
	 */
	public void connect(String server) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);

		player.sendPluginMessage(INSTANCE, "BungeeCord", out.toByteArray());
	}

	// == Managers ====================================================== //

	/**
	 * @return The menu manager instance for the player.
	 */
	public InventoryManager getInvManager() {
		return invManager;
	}

	/**
	 * Sets the holder for the {@link ScoreboardFactory}, which
	 * is {@code null} by default.
	 *
	 * <p>The scoreboard holder will be in charge of what to show
	 * on the scoreboard each time it updates.</p>
	 *
	 * @param	holder
	 * 			The scoreboard holder to set.
	 * @see		ScoreboardHolder
	 * @see		ScoreboardFactory
	 */
	public void setScoreboardHolder(ScoreboardHolder holder) {
		sbHolder = holder;
	}

	@Override
	public ScoreboardFactory getScoreboardLayout() {
		return equals(sbHolder) ? null : sbHolder.getScoreboardLayout();
	}

	// == Essential ====================================================== //

	/**
	 * Terminates the this player's accessibility with the server.
	 */
	public void destroy() {
		List<UpdateOneModel<Document>> operations = Lists.newArrayList();
		Bson searchQuery = Filters.eq("uuid", getUuid());

		// update last logout
		operations.add(new UpdateOneModel<>(searchQuery, new Document(
				DataOperator.$SET.getOperator(),
				new Document(DataInfo.LAST_LOGOUT.getStat(),
						System.currentTimeMillis() / 1000))));

		// update playing
		operations.add(new UpdateOneModel<>(searchQuery, new Document(
				DataOperator.$SET.getOperator(),
				new Document(DataInfo.PLAYING.getStat(), false))));

		// send bulk update and await result
		DatabaseAPI.bulkUpdate(operations, bulkWriteResult -> RiddlesCore.logIf(
				!bulkWriteResult.wasAcknowledged(),
				"Bulk update failed for %s (destroy)",
				getName()
		));

		PLAYER_MANAGER.remove(this);
	}

	/**
	 * Creates a new instance of this player if he doesn't exist,
	 * or returns an existing instance.
	 *
	 * @param	player The delegate to use for this instance.
	 * @return	The single instance of the online player.
	 * @see		CorePlayerManager#add(Player, String)
	 */
	public static CorePlayer createIfAbsent(Player player) {
		return PLAYER_MANAGER.add(Validate.notNull(player, "The player must not be null!"));
	}
}