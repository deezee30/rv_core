/*
 * RiddlesCore
 */

package com.riddlesvillage.core.inventory.item;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.collect.EnhancedMap;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.json.simple.JSONAware;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreItemStack implements JSONAware {

	private final Material material;
	private final int quantity;
	private final String displayName;
	private final int color;
	private final EnhancedList<String> lore = new EnhancedList<>();
	private final EnhancedMap<Enchantment, Integer> enchantments = new EnhancedMap<>();

	public CoreItemStack(ItemStack item) {
		if (item == null) {
			material = Material.AIR;
			quantity = 1;
			displayName = null;
			color = 0;
		} else {
			material = item.getType();
			quantity = item.getAmount();

			if (item.hasItemMeta()) {
				ItemMeta meta = item.getItemMeta();
				color = meta instanceof LeatherArmorMeta ? ((LeatherArmorMeta) meta).getColor().asRGB() : 0;
				displayName = meta.hasDisplayName() ? meta.getDisplayName() : null;
				if (meta.hasLore()) lore.addAll(meta.getLore());
			} else {
				color = 0;
				displayName = null;
			}

			enchantments.putAll(item.getEnchantments());
		}
	}

	public CoreItemStack(ItemStackContainer item) {
		this.material = Material.getMaterial(item.material);
		this.quantity = item.quantity;
		this.displayName = item.title;
		this.color = item.color;
		this.lore.addAll(item.lore);
		for (Map.Entry<String, Integer> entry : item.enchantments.entrySet()) {
			this.enchantments.put(EnchantmentWrapper.getByName(entry.getKey()), entry.getValue());
		}
	}

	public Material getMaterial() {
		return material;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getDisplayName() {
		return displayName;
	}

	public boolean hasColor() {
		return color != 0;
	}

	public Color getColor() {
		if (!hasColor()) return null;
		return Color.fromRGB(color);
	}

	public EnhancedList<String> getLore() {
		return lore;
	}

	public HashMap<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}

	public ItemStack getItemStack() {
		return new ItemBuilder(material, quantity) {{
			setTitle(displayName);
			if (hasColor()) setColor(getColor());
			addLores(lore);
			enchantments.entrySet().stream().forEach(entry -> addEnchantment(entry.getKey(), entry.getValue()));
		}}.build();
	}

	@Override
	public String toString() {
		return "CoreItemStack{" +
				"material=" + material +
				", quantity=" + quantity +
				", displayName='" + displayName + '\'' +
				", color=" + color +
				", lore=" + lore +
				", enchantments=" + enchantments +
				'}';
	}

	@Override
	public String toJSONString() {
		Map<String, Integer> map = Maps.newHashMap();
		for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
			map.put(entry.getKey().getName(), entry.getValue());
		}

		return new Gson().toJson(new ItemStackContainer(
				material.name(),
				quantity,
				displayName,
				color,
				lore,
				map
		));
	}

	public static CoreItemStack fromJSONString(String json) {
		return new CoreItemStack(new Gson().fromJson(json, ItemStackContainer.class));
	}

	public static final class ItemStackContainer {

		private String material;
		private int quantity;
		private String title;
		private int color;
		private List<String> lore;
		private Map<String, Integer> enchantments;

		public ItemStackContainer() {}

		public ItemStackContainer(String material,
								  int quantity,
								  String title,
								  int color,
								  List<String> lore,
								  Map<String, Integer> enchantments) {
			this.material = material;
			this.quantity = quantity;
			this.title = title;
			this.color = color;
			this.lore = lore;
			this.enchantments = enchantments;
		}

		public String getMaterial() {
			return material;
		}

		public int getQuantity() {
			return quantity;
		}

		public String getTitle() {
			return title;
		}

		public int getColor() {
			return color;
		}

		public List<String> getLore() {
			return lore;
		}

		public Map<String, Integer> getEnchantments() {
			return enchantments;
		}
	}
}