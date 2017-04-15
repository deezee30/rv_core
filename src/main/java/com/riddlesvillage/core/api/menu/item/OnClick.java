package com.riddlesvillage.core.api.menu.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Created by matt1 on 3/21/2017.
 */
@FunctionalInterface
public interface OnClick {

    public abstract void onClick(Player player, ClickType clickType);
}
