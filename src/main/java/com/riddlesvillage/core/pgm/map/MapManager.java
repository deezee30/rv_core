/*
 * rv_core
 * 
 * Created on 06 July 2017 at 6:46 PM.
 */

package com.riddlesvillage.core.pgm.map;

import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.pgm.loader.MapLoader;
import org.apache.commons.lang3.Validate;

import java.util.Iterator;

public final class MapManager implements Iterable<LoadedMap> {

    private static final MapManager instance = new MapManager();
    private final EnhancedList<LoadedMap> maps = new EnhancedList<>();
    private final EnhancedList<MapLoader> mapLoaders  = new EnhancedList<>();

    // disable initialization
    private MapManager() {}

    public boolean registerLoader(final MapLoader loader) {
        return mapLoaders.addIf(!mapLoaders.contains(Validate.notNull(loader)), loader);
    }

    public void registerMap(final LoadedMap map) {
        maps.add(map);
    }

    @Override
    public Iterator<LoadedMap> iterator() {
        return maps.iterator();
    }

    public static MapManager getInstance() {
        return instance;
    }
}