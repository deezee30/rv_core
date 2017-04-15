package com.riddlesvillage.core.player.events;

import com.riddlesvillage.core.player.GamePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ValidPlayerQuitEvent extends Event {
    private static HandlerList handlers = new HandlerList();
    private String quitMessage;
    private GamePlayer gamePlayer;

    public ValidPlayerQuitEvent(GamePlayer gamePlayer, String joinMessage) {
        this.gamePlayer = gamePlayer;
        this.quitMessage = joinMessage;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public String getQuitMessage() {
        return quitMessage;
    }

    public void setQuitMessage(String quitMessage) {
        this.quitMessage = quitMessage;
    }
}
