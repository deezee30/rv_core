/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player;

import com.google.common.base.Strings;
import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.player.profile.AbstractCoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;
import java.util.UUID;

public class OfflineCorePlayer extends AbstractCoreProfile {

	/*
	 * Cached profiles may include names/uuids of players that may not exist.
	 * To check if they are a real profile simply call #hasPlayed().
	 */
	private static final EnhancedList<OfflineCorePlayer>	CACHED_PROFILES = new EnhancedList<>();
	private static final CorePlayerManager					ONLINE_PLAYERS	= CorePlayerManager.getInstance();

	static {
		// Refresh cached users every 30 seconds.
		new BukkitRunnable() {

			@Override
			public void run() {
				CACHED_PROFILES.forEach(profile -> profile.refreshStats());
			}
		}.runTaskTimerAsynchronously(RiddlesCore.getInstance(), 600L, 600L);
	}

	private OfflineCorePlayer(UUID uuid, String name) {
		super(uuid, name);
		timer.forceStop();
	}

	@Override
	public String getDisplayName() {
		return ChatColor.BLUE + getName();
	}

	public static OfflineCorePlayer fromName(String name) {
		final Optional<OfflineCorePlayer> cache = get(name);

		if (cache.isPresent()) {
			return cache.get();
		} else {
			OfflineCorePlayer profile = new OfflineCorePlayer(null, name);
			CACHED_PROFILES.addIf(ONLINE_PLAYERS.get(name) == null, profile);
			return profile;
		}
	}

	public static OfflineCorePlayer fromUuid(UUID uuid) {
		final Optional<OfflineCorePlayer> cache = get(uuid);

		if (cache.isPresent()) {
			return cache.get();
		} else {
			OfflineCorePlayer profile = new OfflineCorePlayer(uuid, null);
			CACHED_PROFILES.addIf(ONLINE_PLAYERS.get(uuid) == null, profile);
			return profile;
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
	public double getCoinMultiplier() {
		return 1;
	}

	@Override
	public void setCoinMultiplier(double factor) {
		// do nothing - player offline
	}
}