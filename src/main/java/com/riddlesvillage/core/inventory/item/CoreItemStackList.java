/*
 * RiddlesCore
 */

package com.riddlesvillage.core.inventory.item;

import com.google.gson.Gson;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.collect.EnhancedList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class CoreItemStackList extends EnhancedList<CoreItemStack> {

	public CoreItemStackList() {}

	public CoreItemStackList(ItemStack... itemStacks) {
		for (ItemStack is : itemStacks) add(new CoreItemStack(is));
	}

	public CoreItemStackList(CoreItemStack... itemStacks) {
		for (CoreItemStack is : itemStacks) add(is);
	}

	public CoreItemStackList(ItemStackListContainer container) {
		addAll(container.items);
	}

	public CoreItemStack remove(Material material) {
		return remove(getFirstSlot(material));
	}

	public ItemStack[] toItemStack() {
		ItemStack[] is = new ItemStack[size()];
		for (int x = 0; x < size(); ++x) {
			is[x] = get(x).getItemStack();
		}

		return is;
	}

	public int getFirstSlot(Material mat) {
		for (int x = 0; x < size(); x++) {
			if (get(x) != null) {
				if (get(x).getMaterial().equals(mat)) {
					Messaging.debug("Found %s at slot %s", mat, x);
					return x;
				}
			}
		}

		// if mat has not been found in the list of items return -1
		return -1;
	}

	public void setFirstNull(CoreItemStack item) {
		int x = 0;
		for (CoreItemStack i : this) {
			if (i == null
					|| i.getItemStack() == null
					|| i.getItemStack().getType().equals(Material.AIR)) {
				set(x, item);
				return;
			}
			++x;
		}
	}

	@Override
	public CoreItemStack[] toArray() {
		return toArray(new CoreItemStack[size()]);
	}

	@Override
	public String toJSONString() {
		return new Gson().toJson(new ItemStackListContainer(this));
	}

	public static CoreItemStackList fromJSONString(String json) {
		return new CoreItemStackList(new Gson().fromJson(json, ItemStackListContainer.class));
	}

	public static final class ItemStackListContainer {

		private List<CoreItemStack> items;

		public ItemStackListContainer() {}

		public ItemStackListContainer(List<CoreItemStack> items) {
			this.items = items;
		}
	}
}