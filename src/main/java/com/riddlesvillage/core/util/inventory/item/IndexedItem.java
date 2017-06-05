/*
 * RiddlesCore
 */

package com.riddlesvillage.core.util.inventory.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class IndexedItem extends ItemBuilder {

	private final int slot;

	public IndexedItem(ItemStack item, int slot) {
		super(item);
		this.slot = slot;
	}

	public IndexedItem(Material mat, int slot) {
		super(mat);
		this.slot = slot;
	}

	public IndexedItem(Material mat, int amount, int slot) {
		super(mat, amount);
		this.slot = slot;
	}

	public IndexedItem(Material mat, int amount, short data, int slot) {
		super(mat, amount, data);
		this.slot = slot;
	}

	public IndexedItem(Material mat, short data, int slot) {
		super(mat, data);
		this.slot = slot;
	}

	public final int getSlot() {
		return slot;
	}
}