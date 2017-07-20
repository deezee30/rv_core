package com.riddlesvillage.core.pgm;

import com.riddlesvillage.core.pgm.option.GameOptions;
import com.riddlesvillage.core.pgm.stage.GameStages;
import org.apache.commons.lang3.Validate;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Game {

    protected final String name;
    protected final GameOptions options;
    protected final GameStages stages;
    JavaPlugin plugin;

    public Game(final String name,
                final GameOptions options,
                final GameStages stages) {
        this.name = Validate.notNull(name);
        this.options = Validate.notNull(options);
        this.stages = Validate.notNull(stages);
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