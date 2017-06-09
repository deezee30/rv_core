/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player;

import com.google.common.base.Strings;
import com.mongodb.async.client.MongoCollection;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.database.Database;
import com.riddlesvillage.core.player.profile.AbstractCoreProfile;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OfflineCorePlayer extends AbstractCoreProfile {

	/*
	 * Cached profiles may include names/uuids of players that may not exist.
	 * To check if they are a real profile simply call #hasPlayed().
	 */
	private static final EnhancedList<OfflineCorePlayer> CACHED_PROFILES = new EnhancedList<>();

	static {
		// Refresh cached users every 30 seconds.
		new BukkitRunnable() {

			@Override
			public void run() {
				CACHED_PROFILES.forEach(profile -> profile.refreshStats());
			}
		}.runTaskTimerAsynchronously(RiddlesCore.getInstance(), 600L, 600L);
	}

	private Optional<Runnable>
			doAfter = Optional.empty();
	private transient Rank
			rank	= Rank.DEFAULT;
	private transient boolean
			premium	= false;
	private transient int
			coins	= 0,
			tokens	= 0;

	private OfflineCorePlayer(UUID uuid, String name, Runnable doAfter) {
		super(uuid, name);
		this.doAfter = Optional.ofNullable(doAfter);
	}

	OfflineCorePlayer(CorePlayer player) {
		super(player.getUuid(), player.getName());
	}

	@Override
	public String getDisplayName() {
		return ChatColor.GRAY + getName();
	}

	@Override
	public String getIp() {
		return null;
	}

	@Override
	public List<String> getIpHistory() {
		return Collections.emptyList();
	}

	@Override
	public List<String> getNameHistory() {
		return Collections.emptyList();
	}

	@Override
	public MongoCollection<Document> getCollection() {
		return Database.getMainCollection();
	}

	@Override
	public void onLoad(Optional<Document> doc) {
		if (doc.isPresent()) {
			Document stats = doc.get();

			rank = Rank.byName(stats.getString("rank"));
			premium = stats.getBoolean("premium");
			coins = stats.getInteger("coins");
			tokens = stats.getInteger("tokens");

			CACHED_PROFILES.addIf(!CACHED_PROFILES.contains(this), this);
		} else {
			Messaging.debug("Failed to obtain stats for '%s' ('%s')", getName(), getUuid());
		}

		// run the runnable once
		if (doAfter.isPresent()) {
			doAfter.get().run();
			doAfter = Optional.empty();
		}
	}

	public static OfflineCorePlayer fromName(String name) {
		return fromName(name, null);
	}

	public static OfflineCorePlayer fromName(String name, Runnable doAfter) {
		final Optional<OfflineCorePlayer> cache = get(name);

		if (cache.isPresent()) {
			return cache.get();
		} else {
			return new OfflineCorePlayer(null, name, doAfter);
		}
	}

	public static OfflineCorePlayer fromUuid(UUID uuid) {
		return fromUuid(uuid, null);
	}

	public static OfflineCorePlayer fromUuid(UUID uuid, Runnable doAfter) {
		final Optional<OfflineCorePlayer> cache = get(uuid);

		if (cache.isPresent()) {
			return cache.get();
		} else {
			return new OfflineCorePlayer(uuid, null, doAfter);
		}
	}

	public static void removeFromCache(UUID uuid) {
		Optional<OfflineCorePlayer> profile = get(uuid);
		if (profile.isPresent()) CACHED_PROFILES.remove(profile.get());
	}

	private static Optional<OfflineCorePlayer> get(String name) {
		for (OfflineCorePlayer profile : CACHED_PROFILES) {
			if (!Strings.isNullOrEmpty(profile.getName())
					&& profile.getName().equalsIgnoreCase(name)) {
				return Optional.of(profile);
			}
		}

		return Optional.empty();
	}

	private static Optional<OfflineCorePlayer> get(UUID id) {
		for (OfflineCorePlayer profile : CACHED_PROFILES) {
			if (profile.getUuid() != null
					&& profile.getUuid().equals(id)) {
				return Optional.of(profile);
			}
		}

		return Optional.empty();
	}

	@Override
	public int getCoins() {
		return coins;
	}

	@Override
	public void _setCoins(int coins) {
		this.coins = coins;
	}

	@Override
	public boolean isPremium() {
		return premium;
	}

	@Override
	public void _setPremium(boolean premium) {
		this.premium = premium;
	}

	@Override
	public Rank getRank() {
		return rank;
	}

	@Override
	public void _setRank(Rank rank) {
		this.rank = rank;
	}

	@Override
	public int getTokens() {
		return tokens;
	}

	@Override
	public void _setTokens(int tokens) {
		this.tokens = tokens;
	}
}