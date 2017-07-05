package com.riddlesvillage.core.game.stage;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.game.GameMode;
import com.riddlesvillage.core.game.event.GameModeStageEndEvent;
import com.riddlesvillage.core.game.event.GameModeStageStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Matthew E on 7/5/2017.
 */
public abstract class GameModeStage extends BukkitRunnable {
    private String name;
    private GameMode gameMode;

    public GameModeStage(String name, GameMode gameMode) {
        this.name = name;
        this.gameMode = gameMode;
    }

    public abstract void onEnd();

    public abstract boolean tick();

    public void run() {
        if (tick()) {
            this.end();
            return;
        }
    }

    public void sendMessage(String message) {
        gameMode.sendMessage(message);
    }

    public abstract void onStart();

    public void start() {
        onStart();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.get(), this, 5L, 5L);
        GameModeStageStartEvent stageStartEvent = new GameModeStageStartEvent(gameMode, this);
        Bukkit.getPluginManager().callEvent(stageStartEvent);
    }

    public void end() {
        onEnd();
        if (Bukkit.getScheduler().isCurrentlyRunning(getTaskId())) {
            Bukkit.getScheduler().cancelTask(getTaskId());
        }
        GameModeStageEndEvent stageEndEvent = new GameModeStageEndEvent(gameMode, this);
        Bukkit.getPluginManager().callEvent(stageEndEvent);
    }

    public String getName() {
        return name;
    }
}
