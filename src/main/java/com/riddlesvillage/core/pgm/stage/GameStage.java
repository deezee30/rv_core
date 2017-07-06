package com.riddlesvillage.core.pgm.stage;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.pgm.Game;
import com.riddlesvillage.core.pgm.event.GameStageEndEvent;
import com.riddlesvillage.core.pgm.event.GameStageStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Matthew E on 7/5/2017.
 */
public abstract class GameStage extends BukkitRunnable {
    private String name;
    private Game gameMode;

    public GameStage(String name, Game gameMode) {
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
        GameStageStartEvent stageStartEvent = new GameStageStartEvent(gameMode, this);
        Bukkit.getPluginManager().callEvent(stageStartEvent);
    }

    public void end() {
        onEnd();
        if (Bukkit.getScheduler().isCurrentlyRunning(getTaskId())) {
            Bukkit.getScheduler().cancelTask(getTaskId());
        }
        GameStageEndEvent stageEndEvent = new GameStageEndEvent(gameMode, this);
        Bukkit.getPluginManager().callEvent(stageEndEvent);
    }

    public String getName() {
        return name;
    }
}
