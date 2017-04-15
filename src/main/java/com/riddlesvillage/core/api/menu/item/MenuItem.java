package com.riddlesvillage.core.api.menu.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by matt1 on 3/21/2017.
 */
public interface MenuItem {

    ItemStack build();

    MenuItem named(String name);
    MenuItem data(int data);
    MenuItem lore(List<String> lore);
    MenuItem lore(String[] lore);
    MenuItem amount(int amount);
    MenuItem type(Material material);
    MenuItem click(OnClick onClick);


    void onClick(Player player, ClickType click);

    MenuItem owner(String uuid);
}
