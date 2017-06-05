/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.listener.player;

import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.player.profile.CoreProfile;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

final class PlayerLogin implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(PlayerLoginEvent event) {
		Bukkit.getScheduler().runTaskAsynchronously(
				RiddlesCore.getInstance(),
				() -> CoreProfile.PLAYER_MANAGER.add(event.getPlayer(), event.getAddress().getHostAddress())
		);
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
	}
}