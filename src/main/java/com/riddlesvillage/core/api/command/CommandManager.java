package com.riddlesvillage.core.api.command;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Matthew E on 4/1/2017.
 */
public class CommandManager {

    private static CommandManager instance;

    public static CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

    public CommandManager() {
        instance = this;
    }

    public void registerCommand(JavaPlugin plugin, BaseCommand baseCommand) {
        baseCommand.register(plugin, baseCommand);
    }
}
