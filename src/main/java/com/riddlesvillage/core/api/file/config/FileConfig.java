package com.riddlesvillage.core.api.file.config;

import com.riddlesvillage.core.api.file.FileOperations;
import com.riddlesvillage.core.api.file.Loadable;
import com.riddlesvillage.core.api.file.yaml.INew;
import com.riddlesvillage.core.api.file.yaml.YamlFile;
import com.riddlesvillage.core.api.file.yaml.YamlFileImpl;
import com.riddlesvillage.core.api.file.yaml.YamlLoadException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by matt1 on 3/22/2017.
 */
public class FileConfig implements Loadable, FileOperations, Config, INew {

    private File file;
    private ConfigType configType;
    private HashMap<String, Object> objectMap;
    private boolean isNew;
    private String name;

    public FileConfig(ConfigType configType) {
        this.configType = configType;
        this.objectMap = new HashMap<>();
    }

    public HashMap<String, Object> getObjectMap() {
        return objectMap;
    }

    @Override
    public FileConfig load(String name, String path) {
        this.name = name;
        this.file = new File(path, name + this.configType.getFileExtension());
        switch (configType) {
            case JSON:
                //TODO JSON config
                break;
            case YAML:
                try {
                    YamlFile yamlFile = new YamlFileImpl().load(this.file);
                    this.isNew = yamlFile.isNew();
                    yamlFile.getYamlConfiguration().getKeys(true).forEach(key -> {
                        if (yamlFile.getYamlConfiguration().isSet(key)) {
                            objectMap.put(key, yamlFile.getYamlConfiguration().get(key));
                        }
                    });
                } catch (YamlLoadException e) {
                    e.printStackTrace();
                }
                break;
        }
        return this;
    }

    @Override
    public void unload() throws IOException {
        this.save();
    }

    public File getFile() {
        return file;
    }

    public FileConfig setFile(File file) {
        this.file = file;
        return this;
    }

    public ConfigType getConfigType() {
        return configType;
    }

    public FileConfig setConfigType(ConfigType configType) {
        this.configType = configType;
        return this;
    }

    @Override
    public void delete() {
        this.file.delete();
    }

    @Override
    public void save() throws IOException {
        switch (configType) {
            case JSON:
                //TODO JSON config
                break;
            case YAML:
                YamlFile yamlFile = null;
                try {
                    yamlFile = new YamlFileImpl().load(this.file);
                } catch (YamlLoadException e) {
                    e.printStackTrace();
                }
                ConfigurationSection section = yamlFile.getYamlConfiguration();
                this.objectMap.forEach(section::set);
                yamlFile.save();
                break;
        }
    }

    @Override
    public void rename(String newName) {
        switch (configType) {
            case JSON:
                //TODO JSON config
                break;
            case YAML:
                try {
                    YamlFile yamlFile = new YamlFileImpl().load(this.file);
                    yamlFile.rename(newName);
                } catch (YamlLoadException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void create() {

    }

    @Override
    public Object get(String key) {
        return objectMap.get(key);
    }

    @Override
    public String getString(String key) {
        return (String) get(key);
    }

    @Override
    public long getLong(String key) {
        return (long) get(key);
    }

    @Override
    public int getInteger(String key) {
        return (int) get(key);
    }

    @Override
    public double getDouble(String key) {
        return (double) get(key);
    }

    @Override
    public List<Object> getList(String key) {
        return (List<Object>) get(key);
    }

    @Override
    public List<String> getStringList(String key) {
        return (List<String>) get(key);
    }

    @Override
    public List<Integer> getIntegerList(String key) {
        return (List<Integer>) get(key);
    }

    @Override
    public Location getPlayerLocation(String key) {
        World world = Bukkit.getWorld(getString( key + ".location.world"));
        double x = getDouble(key + ".location.x");
        double y = getDouble(key+".location.y");
        double z = getDouble(key+".location.z");
        float yaw = Float.parseFloat(String.valueOf(getDouble(key + ".location.yaw")));
        float pitch = Float.parseFloat(String.valueOf(getDouble(key +".location.pitch")));
        Location location = new Location(world, x, y, z, yaw, pitch);
        return location;
    }

    @Override
    public Location getBlockLocation(String key) {
        World world = Bukkit.getWorld(getString( key + ".location.world"));
        int x = getInteger(key + ".location.x");
        int y = getInteger(key+".location.y");
        int z = getInteger(key+".location.z");
        Location location = new Location(world, x, y, z);
        return location;
    }


    @Override
    public void set(String key, Object value) {
        if (value instanceof Location) {
            if (objectMap.containsKey(key + ".location.world")) {
                objectMap.remove(key + ".location.world");
            }
            if (objectMap.containsKey(key + ".location.x")) {
                objectMap.remove(key + ".location.x");
            }
            if (objectMap.containsKey(key + ".location.y")) {
                objectMap.remove(key + ".location.y");
            }
            if (objectMap.containsKey(key + ".location.z")) {
                objectMap.remove(key + ".location.z");
            }
            if (objectMap.containsKey(key + ".location.yaw")) {
                objectMap.remove(key + ".location.yaw");
            }
            if (objectMap.containsKey(key + ".location.pitch")) {
                objectMap.remove(key + ".location.pitch");
            }
            Location l = (Location) value;
            objectMap.remove(key + ".location.world");
            objectMap.put(key + ".location.world", l.getWorld().getName());
            objectMap.put(key + ".location.x", l.getX());
            objectMap.put(key + ".location.y", l.getY());
            objectMap.put(key + ".location.z", l.getZ());
            objectMap.put(key + ".location.yaw", l.getYaw());
            objectMap.put(key + ".location.pitch", l.getPitch());
            return;
        } else {
            if (objectMap.containsKey(key)) {
                objectMap.remove(key);
                objectMap.put(key, value);
            } else {
                objectMap.put(key, value);
            }
        }
    }

    @Override
    public boolean isSet(String key) {
        return objectMap.containsKey(key);
    }

    @Override
    public boolean reload() {
        load(file.getName().replace(configType.getFileExtension(),""), file.getParent());
        return true;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Object> getConfigurationSection(String key) {
        HashMap<String, Object> dataMap = new HashMap<>();
        for (String values : dataMap.keySet()) {
            for (String section : values.split("\\.")) {
                if (section.equals(key)) {
                    dataMap.put(section, dataMap.get(section));
                }
            }
        }
        return dataMap;
    }
}
