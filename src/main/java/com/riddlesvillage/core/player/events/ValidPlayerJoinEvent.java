package com.riddlesvillage.core.player.events;

import com.riddlesvillage.core.player.GamePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ValidPlayerJoinEvent extends Event {

    private static HandlerList handlers = new HandlerList();
    private String joinMessage;
    private GamePlayer gamePlayer;

    public ValidPlayerJoinEvent(GamePlayer gamePlayer, String joinMessage) {
        this.gamePlayer = gamePlayer;
        this.joinMessage = joinMessage;
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

    public String getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
    }
}
