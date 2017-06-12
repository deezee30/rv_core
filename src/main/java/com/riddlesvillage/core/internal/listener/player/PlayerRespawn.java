/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.listener.player;

import com.riddlesvillage.core.player.manager.CorePlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

final class PlayerRespawn implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent event) {
		CorePlayerManager.getInstance().get(event).giveLoginItems();
	}
}