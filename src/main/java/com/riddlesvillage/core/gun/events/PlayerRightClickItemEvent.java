package com.riddlesvillage.core.gun.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Matthew E on 6/19/2017.
 */
public class PlayerRightClickItemEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private ItemStack item;
    private boolean cancel;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     */
    public PlayerRightClickItemEvent(Player player, ItemStack item) {
        this.player = player;
        this.item = item;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }
}
