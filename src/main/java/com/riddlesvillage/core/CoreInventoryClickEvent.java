/*
 * RiddlesCore
 */

package com.riddlesvillage.core;

import com.riddlesvillage.core.player.CorePlayer;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface CoreInventoryClickEvent {

	boolean handleInventory(CorePlayer player,
							ItemStack clickedItem,
							int slot);
}