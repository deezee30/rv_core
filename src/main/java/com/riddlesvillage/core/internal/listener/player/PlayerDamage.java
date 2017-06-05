/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.listener.player;

import com.riddlesvillage.core.player.CorePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

final class PlayerDamage implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			CorePlayer player = CorePlayer.PLAYER_MANAGER.get(event);
			event.setCancelled(player != null && !player.isDamageable());
		}
	}
}