package com.riddlesvillage.core.api.builder;

import net.minecraft.server.v1_11_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew E on 4/1/2017.
 */
public class ItemBuilder implements IItemBuilder {

    private ItemStack itemStack;

    public ItemStack build() {
        setTag("item", true);
        addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
        return itemStack;
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder() {
        this.itemStack = new ItemStack(Material.WOOD_AXE);
    }

    @Override
    public IItemBuilder setTag(String key, Object value) {
        NBTTagCompound tagCompound = getTag();
        if (value instanceof String) {
            tagCompound.setString(key, (String) value);
        }
        if (value instanceof Long) {
            tagCompound.setLong(key, (Long) value);
        }
        if (value instanceof Integer) {
            tagCompound.setInt(key, (Integer) value);
        }
        if (value instanceof Boolean) {
            tagCompound.setBoolean(key, (Boolean) value);
        }
        net.minecraft.server.v1_11_R1.ItemStack stack = CraftItemStack.asNMSCopy(this.itemStack);
        stack.setTag(tagCompound);
        this.itemStack = CraftItemStack.asBukkitCopy(stack);
        return this;
    }

    @Override
    public IItemBuilder setItemMeta(ItemMeta itemMeta) {
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    @Override
    public IItemBuilder setFood() {
        return setTag("psFood", true);
    }

    @Override
    public IItemBuilder setPotion() {
        return setTag("psPotion", true);
    }

    @Override
    public ItemMeta getItemMeta() {
        return itemStack.getItemMeta();
    }

    @Override
    public IItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    @Override
    public IItemBuilder setDisplayName(String name) {
        ItemMeta itemMeta = getItemMeta();
        itemMeta.setDisplayName(name);
        setItemMeta(itemMeta);
        return this;
    }

    @Override
    public IItemBuilder setData(int data) {
        this.itemStack.setDurability((short) data);
        return this;
    }

    @Override
    public IItemBuilder setLore(List<String> loreList) {
        ItemMeta itemMeta = getItemMeta();
        itemMeta.setLore(loreList);
        setItemMeta(itemMeta);
        return this;
    }

    @Override
    public IItemBuilder addLore(String lore) {
        List<String> loreList = (getItemMeta().hasLore()) ? getItemMeta().getLore() : new ArrayList<>();
        loreList.add(lore);
        setLore(loreList);
        return this;
    }

    @Override
    public IItemBuilder addItemFlag(ItemFlag itemFlag) {
        ItemMeta itemMeta = getItemMeta();
        itemMeta.addItemFlags(itemFlag);
        setItemMeta(itemMeta);
        return this;
    }

    @Override
    public IItemBuilder removeItemFlag(ItemFlag itemFlag) {
        ItemMeta itemMeta = getItemMeta();
        itemMeta.removeItemFlags(itemFlag);
        setItemMeta(itemMeta);
        return this;
    }

    @Override
    public IItemBuilder setType(Material type) {
        this.itemStack.setType(type);
        return this;
    }

    @Override
    public NBTTagCompound getTag() {
        net.minecraft.server.v1_11_R1.ItemStack stack = CraftItemStack.asNMSCopy(this.itemStack);
        NBTTagCompound tagCompound = (stack.hasTag()) ? stack.getTag() : new NBTTagCompound();
        return tagCompound;
    }
}
