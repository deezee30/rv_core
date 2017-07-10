package com.riddlesvillage.core.pgm;

import com.riddlesvillage.core.pgm.option.GameOptions;
import com.riddlesvillage.core.pgm.stage.GameStages;
import org.apache.commons.lang3.Validate;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Game {

    protected String name;
    protected GameOptions options = new GameOptions();
    protected GameStages stages = new GameStages();
    JavaPlugin plugin;

    Game(final String name) {
        this.name = Validate.notNull(name);
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

    public JavaPlugin getPlugin() {
        return plugin;
    }
}