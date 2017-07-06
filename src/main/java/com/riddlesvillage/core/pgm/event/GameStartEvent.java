/*
 * RiddlesCore
 */

package com.riddlesvillage.core.pgm.event;

import com.riddlesvillage.core.pgm.Game;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class GameStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Game gameMode;

    public GameStartEvent(Game gameMode) {
        this.gameMode = Validate.notNull(gameMode);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Game getGameMode() {
        return gameMode;
    }
}