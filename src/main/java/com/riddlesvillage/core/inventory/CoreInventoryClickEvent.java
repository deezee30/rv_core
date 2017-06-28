/*
 * RiddlesCore
 */

package com.riddlesvillage.core.inventory;

import com.riddlesvillage.core.player.CorePlayer;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface CoreInventoryClickEvent {

    boolean handleInventory(final CorePlayer player,
                            final ItemStack clickedItem,
                            final int slot);
}