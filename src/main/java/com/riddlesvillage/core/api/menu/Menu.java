package com.riddlesvillage.core.api.menu;

import org.bukkit.inventory.Inventory;

/**
 * Created by matt1 on 3/21/2017.
 */
public interface Menu extends IInventory, Closable, Openable, Refreshable  {

    Inventory getInventory();
    String getTitle();
    int getSlots();
}
