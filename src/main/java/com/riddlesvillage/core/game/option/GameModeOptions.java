package com.riddlesvillage.core.game.option;

import com.riddlesvillage.core.world.region.flag.Flag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthew E on 7/5/2017.
 */
public class GameModeOptions {
    private Map<String, Flag> flagMap;

    public GameModeOptions() {
        this.flagMap = new HashMap<>();
    }

    public void add(Flag flag) {
        if (!flagMap.containsKey(flag.getName())) {
            flagMap.put(flag.getName(), flag);
        }
    }

    public void remove(Flag flag) {
        if (flagMap.containsKey(flag.getName())) {
            flagMap.remove(flag.getName());
        }
    }

    public List<Flag> getFlagList() {
        return new ArrayList<>(flagMap.values());
    }
}
