/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.listener.player;

import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.event.CorePlayerPostLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

final class PlayerLoginFullCheck implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(CorePlayerPostLoadEvent event) {
		Bukkit.getScheduler().runTaskLater(RiddlesCore.getInstance(), () -> {
			final CorePlayer player = event.getProfile();
			if (Bukkit.getMaxPlayers() <= Bukkit.getOnlinePlayers().size() - 1 && !(player.isPremium() || player.isMod())) {
				player.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&',
						RiddlesCore.getSettings().get(player.getLocale(), "player.error.server-full")
				));
				player.destroy();
			}
		}, 4L);
	}
}