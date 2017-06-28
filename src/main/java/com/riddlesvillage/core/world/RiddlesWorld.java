package com.riddlesvillage.core.world;

import com.riddlesvillage.core.Core;
import org.apache.commons.lang3.Validate;
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
        this.worldName = Validate.notNull(worldName);
    }

    public boolean deleteWorld(File worldFile) {
        if (Validate.notNull(worldFile).exists()) {

            File files[] = worldFile.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteWorld(file);
                } else {
                    file.delete();
                }
            }

            isLoaded = false;
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
            Core.log(world.getName() + " unloaded!");
            isLoaded = false;

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
