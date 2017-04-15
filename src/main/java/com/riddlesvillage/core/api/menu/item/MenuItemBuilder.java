package com.riddlesvillage.core.api.menu.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by matt1 on 3/21/2017.
 */
public class MenuItemBuilder implements MenuItem {

    private ItemStack itemStack;
    private OnClick onClick;

    public MenuItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public MenuItemBuilder() {
        this.itemStack = new ItemStack(Material.DIRT);
    }

    public MenuItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    @Override
    public ItemStack build() {
        return itemStack;
    }

    @Override
    public MenuItem named(String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    @Override
    public MenuItem data(int data) {
        this.itemStack.setDurability((short) data);
        return this;
    }

    @Override
    public MenuItem lore(List<String> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> loreList = (itemMeta.hasLore()) ? itemMeta.getLore() : new ArrayList<>();
        loreList.addAll(lore);
        List<String> newLoreList = new ArrayList<>();
        for (String l : loreList) {
            newLoreList.add(ChatColor.translateAlternateColorCodes('&', l));
        }
        itemMeta.setLore(newLoreList);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    @Override
    public MenuItem lore(String[] lore) {
        return lore(Arrays.asList(lore));
    }

    @Override
    public MenuItem amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }


    @Override
    public MenuItem type(Material material) {
        this.itemStack.setType(material);
        return this;
    }

    @Override
    public MenuItem click(OnClick onClick) {
        this.onClick = onClick;
        return this;
    }

    @Override
    public void onClick(Player player, ClickType click) {
        onClick.onClick(player, click);
    }

    @Override
    public MenuItem owner(String name) {
        if (itemStack.getType() == Material.SKULL_ITEM) {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setOwner(name);
            itemStack.setItemMeta(skullMeta);
            return this;
        }
        return this;
    }
}
