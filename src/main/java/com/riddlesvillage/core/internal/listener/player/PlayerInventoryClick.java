/*
 * rv_core
 */

package com.riddlesvillage.core.internal.listener.player;

import com.riddlesvillage.core.CoreInventoryClickEvent;
import com.riddlesvillage.core.CoreSettings;
import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.CorePlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

final class PlayerInventoryClick implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event) {
		ItemStack is = event.getCurrentItem();

		if (is == null || is.getType().equals(Material.AIR)) {
			return;
		}

		Player bPlayer = (Player) event.getWhoClicked();
		CorePlayer player = CorePlayerManager.getInstance().get(bPlayer.getName());
		String locale = player.getLocale();

		CoreSettings settings = RiddlesCore.getSettings();

		for (Map.Entry<String, CoreInventoryClickEvent> entry : settings.getRegisteredInventories().entrySet()) {
			if (event.getInventory().getName().equals(settings.get(locale, entry.getKey()))) {
				event.setCancelled(true);
				if (entry.getValue().handleInventory(player, is, event.getSlot())) {
					bPlayer.closeInventory();
				}

				return;
			}
		}
	}
}