/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.listener.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static com.riddlesvillage.core.player.CorePlayer.PLAYER_MANAGER;

final class PlayerQuit implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);

		PLAYER_MANAGER.get(event.getPlayer().getUniqueId()).destroy();
	}
}