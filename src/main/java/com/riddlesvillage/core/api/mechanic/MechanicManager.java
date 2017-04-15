package com.riddlesvillage.core.api.mechanic;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * Created by Matthew E on 4/1/2017.
 */
public class MechanicManager {

    private static MechanicManager instance;
    private HashMap<String, GameMechanic> mechanicHashMap;

    public static MechanicManager getInstance() {
        if (instance == null) {
            instance = new MechanicManager();
        }
        return instance;
    }

    public MechanicManager() {
        instance = this;
        this.mechanicHashMap = Maps.newHashMap();
    }

    public void registerMechanic(GameMechanic gameMechanic) {
        this.mechanicHashMap.put(gameMechanic.getClass().getCanonicalName(), gameMechanic);
    }

    public void registerMechanics(JavaPlugin plugin) {
        this.mechanicHashMap.values().forEach(gameMechanic -> {
            Bukkit.getServer().getPluginManager().registerEvents(gameMechanic, plugin);
            gameMechanic.onEnable();
        });
    }

    public void disableMechanics() {
        this.mechanicHashMap.values().forEach(gameMechanic -> {
            gameMechanic.onDisable();
            HandlerList.unregisterAll(gameMechanic);
        });
    }
}
