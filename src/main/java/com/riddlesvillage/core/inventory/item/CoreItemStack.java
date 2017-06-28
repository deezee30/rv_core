/*
 * RiddlesCore
 */

package com.riddlesvillage.core.inventory.item;

import com.google.gson.Gson;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.collect.EnhancedMap;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
    private final EnhancedList<String> lore = new EnhancedList<>();
    private final EnhancedMap<Enchantment, Integer> enchantments = new EnhancedMap<>();
    private final int color;

    public CoreItemStack(final ItemStack item) {
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

    public CoreItemStack(final ItemStackContainer item) {
        Validate.notNull(item);
        this.material = Material.getMaterial(item.material);
        this.quantity = item.quantity;
        this.displayName = item.title;
        this.color = item.color;
        this.lore.addAll(item.lore);
        for (Map.Entry<String, Integer> entry : item.enchantments.entrySet()) {
            this.enchantments.put(EnchantmentWrapper.getByName(entry.getKey()), entry.getValue());
        }
    }

    public boolean hasColor() {
        return color != 0;
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

    public EnhancedList<String> getLore() {
        return lore;
    }

    public EnhancedMap<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public Color getColor() {
        if (!hasColor()) return null;
        return Color.fromRGB(color);
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
    public String toJSONString() {
        Map<String, Integer> map = new HashMap<>(enchantments.size());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CoreItemStack that = (CoreItemStack) o;

        return new EqualsBuilder()
                .append(quantity, that.quantity)
                .append(color, that.color)
                .append(material, that.material)
                .append(displayName, that.displayName)
                .append(lore, that.lore)
                .append(enchantments, that.enchantments)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(material)
                .append(quantity)
                .append(displayName)
                .append(lore)
                .append(enchantments)
                .append(color)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("material", material)
                .append("quantity", quantity)
                .append("displayName", displayName)
                .append("lore", lore)
                .append("enchantments", enchantments)
                .append("color", color)
                .toString();
    }

    public static CoreItemStack fromJSONString(String json) {
        return new CoreItemStack(new Gson().fromJson(json, ItemStackContainer.class));
    }

    public static final class ItemStackContainer {

        private final String material;
        private final int quantity;
        private final String title;
        private final int color;
        private final List<String> lore;
        private final Map<String, Integer> enchantments;

        public ItemStackContainer(final String material,
                                  final int quantity,
                                  final String title,
                                  final int color,
                                  final List<String> lore,
                                  final Map<String, Integer> enchantments) {
            this.material = Validate.notNull(material);
            this.quantity = Validate.notNull(quantity);
            this.title = Validate.notNull(title);
            this.color = Validate.notNull(color);
            this.lore = Validate.notNull(lore);
            this.enchantments = Validate.notNull(enchantments);
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

        public String getMaterial() {

            return material;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("material", material)
                    .append("quantity", quantity)
                    .append("title", title)
                    .append("color", color)
                    .append("lore", lore)
                    .append("enchantments", enchantments)
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            ItemStackContainer that = (ItemStackContainer) o;

            return new EqualsBuilder()
                    .append(quantity, that.quantity)
                    .append(color, that.color)
                    .append(material, that.material)
                    .append(title, that.title)
                    .append(lore, that.lore)
                    .append(enchantments, that.enchantments)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(material)
                    .append(quantity)
                    .append(title)
                    .append(color)
                    .append(lore)
                    .append(enchantments)
                    .toHashCode();
        }
    }
}