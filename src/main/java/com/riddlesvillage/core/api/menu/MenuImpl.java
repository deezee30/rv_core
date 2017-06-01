package com.riddlesvillage.core.api.menu;

import com.riddlesvillage.core.api.menu.item.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * Created by matt1 on 3/21/2017.
 */
public class MenuImpl implements Menu, Listener {

    private Inventory inventory;
    private JavaPlugin javaPlugin;
    private HashMap<Integer, MenuItem> itemMap;

    public MenuImpl(String title, int size, JavaPlugin javaPlugin) {
        this.inventory = Bukkit.createInventory(null, size, title);
        this.javaPlugin = javaPlugin;
        this.itemMap = new HashMap<>();
    }

    public void close(Player player) {
        if (player.getOpenInventory().getTopInventory().getTitle().equals(getTitle())) {
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            close((Player) event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getInitiator().getType() == InventoryType.PLAYER) {
            if (event.getDestination().getTitle().equals(getTitle())) {
                event.setCancelled(true);
            }
        }
    }

    public void open(Player player) {
        Bukkit.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
        player.openInventory(this.inventory);
    }

    public void refresh(Player player) {
        player.closeInventory();
        open(player);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getTitle() {
        return inventory.getTitle();
    }

    public int getSlots() {
        return inventory.getSize();
    }

    @Override
    public void addItem(MenuItem menuItem) {
        inventory.addItem(menuItem.build());
    }

    @Override
    public void setItem(int slot, MenuItem menuItem) {
        inventory.setItem((slot - 1), menuItem.build());
        itemMap.put(slot, menuItem);
    }

    @Override
    public void removeItem(int slot) {
        inventory.removeItem(inventory.getItem(slot - 1));
        itemMap.remove(slot);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
            return;
        }
        Inventory inventory = event.getInventory();
        if (inventory.getType() == InventoryType.PLAYER) {
            return;
        }
        if (inventory.getTitle().equals(getTitle())) {
            event.setCancelled(true);
            if (event.getClickedInventory().getTitle().equals(getTitle())) {
                MenuItem menuItem = itemMap.get(event.getSlot() + 1);
                if (menuItem == null) {
                    return;
                }
                menuItem.onClick(player, event.getClick());
            }
        }
    }
}
