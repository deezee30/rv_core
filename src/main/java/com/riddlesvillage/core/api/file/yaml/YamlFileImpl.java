package com.riddlesvillage.core.api.file.yaml;

import com.riddlesvillage.core.api.builder.IItemBuilder;
import com.riddlesvillage.core.api.builder.ItemBuilder;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matt1 on 3/22/2017.
 */
public class YamlFileImpl implements YamlFile {

    private File file;
    private YamlConfiguration yamlConfiguration;
    private boolean isNew;

    @Override
    public void delete() {
        this.file.delete();
    }

    @Override
    public void save() throws IOException {
        this.yamlConfiguration.save(this.file);
    }

    @Override
    public boolean isSet(String key) {
        return get().isSet(key);
    }

    @Override
    public void rename(String newName) {
        this.file.renameTo(new File(file.getParent() + "/", newName));
        try {
            load(this.file);
        } catch (YamlLoadException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create() {
        try {
            this.file.createNewFile();
            this.isNew = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public YamlFile load(File file) throws YamlLoadException {
        this.file = file;
        File path = file.getParentFile();
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!this.file.exists()) {
            this.create();
        }
        try {
            this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.file);
        } catch (Exception e) {
            throw new YamlLoadException("Could not load yaml file " + e.getLocalizedMessage(), e.getCause());
        }
        return this;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public YamlConfiguration getYamlConfiguration() {
        return yamlConfiguration;
    }


    @Override
    public void set(String key, Object value) {
        if (value instanceof Location) {
            Location location = (Location) value;
            this.get().set(key + ".location.world", location.getWorld().getName());
            this.get().set(key + ".location.x", location.getX());
            this.get().set(key + ".location.y", location.getY());
            this.get().set(key + ".location.z", location.getZ());
            this.get().set(key + ".location.yaw", location.getYaw());
            this.get().set(key + ".location.pitch", location.getPitch());
            try {
                this.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        this.yamlConfiguration.set(key, value);
        try {
            this.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ItemStack getItem(String key) {
        ConfigurationSection section = this.get().getConfigurationSection(key + ".item");
        List<String> loreList = section.getStringList("lore");
        List<String> newLoreList = new ArrayList<>();
        loreList.forEach(lore -> newLoreList.add(ChatColor.translateAlternateColorCodes('&', lore)));
        IItemBuilder itemBuilder = new ItemBuilder()
                .setType(Material.valueOf(section.getString("type")))
                .setAmount(section.getInt("amount"))
                .setData(section.getInt("data"))
                .setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("displayName")))
                .setLore(newLoreList);
        return itemBuilder.build();
    }

    @Override
    public YamlConfiguration get() {
        return yamlConfiguration;
    }

    @Override
    public Location getLocation(String key) {
        World world = Bukkit.getWorld(yamlConfiguration.getString(key +".location.world"));
        double x = yamlConfiguration.getDouble(key+".location.x");
        double y = yamlConfiguration.getDouble(key+".location.y");
        double z = yamlConfiguration.getDouble(key+".location.z");
        double yaw = yamlConfiguration.getDouble(key+".location.yaw");
        double pitch = yamlConfiguration.getDouble(key+".location.pitch");
        Location location = new Location(world, x, y, z, Float.valueOf(String.valueOf(yaw)), Float.valueOf(String.valueOf(pitch)));
        return location;
    }

    public Location getSpawnerLocation(ConfigurationSection yamlConfiguration) {
        String parse = yamlConfiguration.getString("location");
        String[] data = parse.split(":");
        String world = data[0];
        int x = Integer.parseInt(data[1]);
        int y = Integer.parseInt(data[2]);
        int z = Integer.parseInt(data[3]);
        Location location = new Location(Bukkit.getWorld(world), x, y, z);
        return location;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}
