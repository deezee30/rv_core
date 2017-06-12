/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.manager;

import com.google.common.collect.ImmutableList;
import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.inventory.item.IndexedItem;
import com.riddlesvillage.core.inventory.item.ItemBuilder;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class InventoryManager {

	private final CorePlayer player;

	public InventoryManager(CorePlayer player) {
		this.player = player;
	}

	public void createLocaleMenu() {
		ImmutableList<String> locales = RiddlesCore.getSettings().getLocales();

		Inventory inv = ci(
				player,
				getFitAmount(locales.size()),
				"menu.locale"
		);

		for (int x = 0; x < locales.size(); ++x) {
			String language = locales.get(x);

			inv.setItem(x, new ItemBuilder(Material.BOOK) {{
				setTitle(ChatColor.AQUA + WordUtils.capitalize(language));
			}}.build());
		}

		player.getPlayer().openInventory(inv);
	}

	private Inventory setItem(Inventory inventory, IndexedItem item) {
		inventory.setItem(item.getSlot(), item.buildWithLocaleSupport(player.getLocale()));
		return inventory;
	}

	public static Inventory ci(CorePlayer player, int rows, String title) {
		return Bukkit.getServer().createInventory(
				player.getBukkitPlayer(),
				Math.abs(rows * 9) > 54
						? 54
						: Math.abs(rows * 9),
				RiddlesCore.getSettings().get(player.getLocale(), title)
		);
	}

	public static int getFitAmount(int x) {
		int y = 1;
		for (int z = 1; z <= 6; ++z)
			if (x > 9 * z) ++y;
		return y;
	}
}