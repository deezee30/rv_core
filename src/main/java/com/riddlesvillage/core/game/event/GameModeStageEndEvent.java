/*
 * RiddlesCore
 */

package com.riddlesvillage.core.game.event;

import com.riddlesvillage.core.game.GameMode;
import com.riddlesvillage.core.game.stage.GameModeStage;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class GameModeStageEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final GameMode gameMode;
    private final GameModeStage stage;

    public GameModeStageEndEvent(GameMode gameMode, GameModeStage stage) {
        this.gameMode = Validate.notNull(gameMode);
        this.stage = Validate.notNull(stage);
    }

    public GameModeStage getStage() {
        return stage;
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