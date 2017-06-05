/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.listener.player;

import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.player.CorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

final class PlayerDropItem implements Listener {

	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerItemDrop(PlayerDropItemEvent event) {
		String locale = CorePlayer.createIfAbsent(event.getPlayer()).getLocale();
		ItemStack stack = event.getItemDrop().getItemStack();

		RiddlesCore.getSettings().getLoginItems().keySet().stream()
				.filter(item -> stack.equals(item.buildWithLocaleSupport(locale)))
				.forEach(item -> event.setCancelled(true));
	}
}