/*
 * rv_core
 * 
 * Created on 06 July 2017 at 1:20 PM.
 */

package com.riddlesvillage.core.pgm;

import com.riddlesvillage.core.pgm.map.LoadedMap;
import com.riddlesvillage.core.world.region.Region;
import org.apache.commons.lang3.Validate;

public class Arena {

    private final LoadedMap map;

    private Region region;

    public Arena(final LoadedMap map) {
        this.map = Validate.notNull(map);
    }

    public LoadedMap getMap() {
        return map;
    }

    public Region getRegion() {
        return region;
    }
}