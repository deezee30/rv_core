package com.riddlesvillage.core.pgm;

import com.riddlesvillage.core.pgm.event.GameStartEvent;
import com.riddlesvillage.core.pgm.option.GameOptions;
import com.riddlesvillage.core.pgm.player.GamePlayerList;
import com.riddlesvillage.core.pgm.stage.GameStages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Matthew E on 7/5/2017.
 */
public abstract class Game {

    protected String name;
    protected GameOptions options;
    protected GameStages stages;
    protected int maxPlayers;
    protected GamePlayerList playerList;
    JavaPlugin plugin;

    public Game(String name) {
        this.name = name;
        this.init();
    }

    protected void init() {
        this.playerList = new GamePlayerList();
        this.options = new GameOptions();
        this.stages = new GameStages();
        this.initGameMode();
        GameStartEvent gameModeInitEvent = new GameStartEvent(this);
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

    public String getName() {
        return name;
    }

    public GameOptions getOptions() {
        return options;
    }

    public GameStages getStages() {
        return stages;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public GamePlayerList getPlayerList() {
        return playerList;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}