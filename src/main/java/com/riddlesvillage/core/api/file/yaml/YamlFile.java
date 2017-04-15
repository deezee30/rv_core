package com.riddlesvillage.core.api.file.yaml;

import com.riddlesvillage.core.api.file.FileOperations;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

/**
 * Created by matt1 on 3/22/2017.
 */
public interface YamlFile extends FileOperations, YamlLoadable, INew {

    File getFile();

    YamlConfiguration getYamlConfiguration();

    void set(String key, Object to);

    ItemStack getItem(String key);

    Location getLocation(String load);

    YamlConfiguration get();

    Location getSpawnerLocation(ConfigurationSection section);

    void save() throws IOException;

    boolean isSet(String key);
}
