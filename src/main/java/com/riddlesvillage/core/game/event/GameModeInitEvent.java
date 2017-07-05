/*
 * RiddlesCore
 */

package com.riddlesvillage.core.game.event;

import com.riddlesvillage.core.game.GameMode;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class GameModeInitEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final GameMode gameMode;

    public GameModeInitEvent(GameMode gameMode) {
        this.gameMode = Validate.notNull(gameMode);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public GameMode getGameMode() {
        return gameMode;
    }
}