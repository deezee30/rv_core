package com.riddlesvillage.core.api.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Matthew E on 4/1/2017.
 */
public abstract class BaseCommand implements CommandExecutor {

    private String name;

    public BaseCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            return execute((Player) sender, args);
        }
        return true;
    }

    public abstract boolean execute(Player sender, String[] args);

    public void register(JavaPlugin plugin, BaseCommand command) {
        plugin.getCommand(command.getName()).setExecutor(command);
    }
}
