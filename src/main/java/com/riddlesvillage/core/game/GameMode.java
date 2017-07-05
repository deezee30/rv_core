package com.riddlesvillage.core.game;

import com.riddlesvillage.core.game.event.GameModeInitEvent;
import com.riddlesvillage.core.game.option.GameModeOptions;
import com.riddlesvillage.core.game.player.GamePlayerList;
import com.riddlesvillage.core.game.stage.GameModeStages;
import org.bukkit.Bukkit;

/**
 * Created by Matthew E on 7/5/2017.
 */
public abstract class GameMode {
    protected String name;
    protected GameModeOptions options;
    protected GameModeStages stages;
    protected int maxPlayers;
    protected GamePlayerList playerList;

    public GameMode(String name) {
        this.name = name;
        this.init();
    }

    protected void init() {
        this.playerList = new GamePlayerList();
        this.options = new GameModeOptions();
        this.stages = new GameModeStages();
        this.initGameMode();
        GameModeInitEvent gameModeInitEvent = new GameModeInitEvent(this);
        Bukkit.getPluginManager().callEvent(gameModeInitEvent);
    }

    public abstract void initGameMode();

    public void sendMessage(String message) {
        playerList.forEach(gamePlayer -> {
            if (gamePlayer.getCorePlayer().isOnline()) {
                gamePlayer.getCorePlayer().sendMessage(message);
            }
        });
    }
}
