package com.riddlesvillage.core.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * Created by Matthew E on 6/14/2017.
 */
public class RiddlesWorld {
    private String worldName;
    private boolean isLoaded;

    public RiddlesWorld(String worldName) {
        this.worldName = worldName;
    }

    public boolean deleteWorld(File worldFile) {
        if (worldFile.exists()) {
            File files[] = worldFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteWorld(files[i]);
                } else {
                    files[i].delete();
                }
            }
            this.isLoaded = false;
        }
        return (worldFile.delete());
    }

    public World getWorld() {
        if (this.isLoaded)  {
            return Bukkit.getWorld(worldName);
        }
        return null;
    }

    public boolean unloadWorld() {
        World world = getWorld();
        if (world != null) {
            for (Player player : world.getPlayers()) {
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            }
            Bukkit.getServer().unloadWorld(world, false);
            System.out.println(world.getName() + " unloaded!");
            this.isLoaded = false;
            return true;
        }
        return false;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public RiddlesWorld setLoaded(boolean loaded) {
        isLoaded = loaded;
        return this;
    }
}
