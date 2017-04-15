package com.riddlesvillage.core.api.file.config;

import org.bukkit.Location;

import java.util.List;

/**
 * Created by matt1 on 3/22/2017.
 */
public interface Config {

    Object get(String key);

    String getString(String key);

    long getLong(String key);

    int getInteger(String key);

    double getDouble(String key);

    List<Object> getList(String key);

    List<String> getStringList(String key);

    List<Integer> getIntegerList(String key);

    Location getBlockLocation(String key);

    Location getPlayerLocation(String key);

    void set(String key, Object value);

    boolean isSet(String key);

    boolean reload();
}

