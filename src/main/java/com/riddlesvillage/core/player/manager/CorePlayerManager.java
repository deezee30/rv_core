/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.manager;

import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.CorePlayerList;
import com.riddlesvillage.core.player.OfflineCorePlayer;
import com.riddlesvillage.core.player.profile.AbstractCoreProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityEvent;

import java.util.UUID;

public final class CorePlayerManager extends PlayerManager<CorePlayer> {

	private static CorePlayerManager instance;
	private static final CorePlayerList PLAYERS = new CorePlayerList();

	private CorePlayerManager() {}

	public CorePlayer get(EntityEvent event) {
		return get(event.getEntity().getUniqueId());
	}

	public AbstractCoreProfile getOrOffline(UUID id) {
		final CorePlayer player = get(id);
		return player == null ? OfflineCorePlayer.fromUuid(id) : player;
	}

	public AbstractCoreProfile getOrOffline(String name) {
		final CorePlayer player = get(name);
		return player == null ? OfflineCorePlayer.fromName(name) : player;
	}

	@Override
	protected CorePlayerList delegate() {
		return PLAYERS;
	}

	/**
	 * {@inheritDoc}
	 * @see #add(Player, String)
	 */
	@Override
	public CorePlayer add(Player player) {
		return add(player, player.getAddress().getHostName());
	}

	/**
	 * Registers the player along with his hostname.
	 *
	 * <p>This should be used when the {@param player}'s
	 * {@link Player#getAddress()} is still undefined.  This
	 * usually happens before the {@code CraftPlayer} instance
	 * isn't fully loaded, ie, when the player is about to join
	 * the server ({@link org.bukkit.event.player.PlayerLoginEvent})</p>
	 *
	 * <p>If the player is already registered, the current
	 * registered instance will be returned.</p>
	 *
	 * <p>A new instance of {@link CorePlayer} will be instantiated provided
	 * the player is not already online - which is a quite heavy task and
	 * should be dealt with accordingly.</p>
	 *
	 * @param	player
	 * 			The player ready to log in.
	 * @param	hostName
	 * 			A valid given host name of the player.
	 * @return	A unique {@code CorePlayer} instance.
	 * @throws	com.riddlesvillage.core.CoreException
	 * 			If a database error occurs.
	 */
	public CorePlayer add(Player player, String hostName) {
		CorePlayer rPlayer = get(player.getName());
		Bukkit.getOfflinePlayer(player.getUniqueId());

		if (rPlayer != null) {
			return rPlayer;
		}

		// Player's name and stats may have changed since his last query - Remove him from cache
		OfflineCorePlayer.removeFromCache(player.getUniqueId());
		PLAYERS.add(rPlayer = CorePlayer._init(player, hostName));
		return rPlayer;
	}

	public static CorePlayerManager getInstance() {
		return instance == null ? instance = new CorePlayerManager() : instance;
	}
}