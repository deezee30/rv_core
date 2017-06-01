/*
 * rv_core
 * 
 * Created on 01 June 2017 at 11:57 PM.
 */

package com.riddlesvillage.core.player;

import com.riddlesvillage.core.api.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityEvent;

public final class GamePlayerManager extends PlayerManager<GamePlayer> {

	private static GamePlayerManager instance;
	private static final GamePlayerList PLAYERS = new GamePlayerList();

	// disable public initialization
	private GamePlayerManager() {}

	public GamePlayer get(EntityEvent event) {
		return get(event.getEntity().getUniqueId());
	}

	@Override
	protected GamePlayerList delegate() {
		return PLAYERS;
	}

	/**
	 * Registers the player along with his hostname.
	 *
	 * <p>This should be used when the {@param player}'s
	 * {@link Player#getAddress()} is still undefined.  This
	 * usually happens before the {@code CraftPlayer} instance
	 * is fully loaded, ie, when the player is about to join
	 * the server ({@link org.bukkit.event.player.PlayerLoginEvent})</p>
	 *
	 * <p>If the player is already registered, the current
	 * registered instance will be returned.</p>
	 *
	 * <p>A new instance of {@link GamePlayer} will be instantiated provided
	 * the player is not already online - which is a quite heavy task and
	 * should be dealt with accordingly.</p>
	 *
	 * @param    player The player ready to log in.
	 * @return   A unique {@code GamePlayer} instance.
	 */
	public GamePlayer add(Player player) {
		GamePlayer gPlayer = get(player.getName());
		Bukkit.getOfflinePlayer(player.getUniqueId());

		if (gPlayer != null) {
			return gPlayer;
		}

		PLAYERS.add(gPlayer = new GamePlayer(player));
		return gPlayer;
	}

	public static GamePlayerManager get() {
		return instance == null ? instance = new GamePlayerManager() : instance;
	}
}