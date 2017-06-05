/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.listener.player;

import org.bukkit.event.Listener;

public final class PlayerListeners {

	// Disable initialization
	private PlayerListeners() {}

	public static synchronized Listener[] get() {
		return new Listener[] {
				new PlayerChat(),
				new PlayerChat(),
				new PlayerDamage(),
				new PlayerDeath(),
				new PlayerDropItem(),
				new PlayerFoodChange(),
				new PlayerInventoryClick(),
				new PlayerJump(),
				new PlayerLogin(),
				new PlayerLoginFullAllow(),
				new PlayerLoginFullCheck(),
				new PlayerMove(),
				new PlayerQuit(),
				new PlayerRespawn()
		};
	}
}