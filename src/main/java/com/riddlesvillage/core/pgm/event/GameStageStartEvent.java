/*
 * RiddlesCore
 */

package com.riddlesvillage.core.pgm.event;

import com.riddlesvillage.core.pgm.Game;
import com.riddlesvillage.core.pgm.stage.GameStage;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class GameStageStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Game gameMode;
    private final GameStage stage;

    public GameStageStartEvent(Game gameMode, GameStage stage) {
        this.gameMode = Validate.notNull(gameMode);
        this.stage = Validate.notNull(stage);
    }

    public GameStage getStage() {
        return stage;
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