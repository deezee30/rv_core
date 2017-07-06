/*
 * rv_core
 * 
 * Created on 06 July 2017 at 1:01 PM.
 */

package com.riddlesvillage.core.pgm.map;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.pgm.loader.MapLoader;
import com.riddlesvillage.core.pgm.loader.MapLoaderException;
import com.riddlesvillage.core.world.region.Region;
import com.riddlesvillage.core.world.region.Regions;
import org.apache.commons.lang3.Validate;

import java.util.List;

public abstract class PlayingMap {

    private String name;
    private List<MapObjective> objectives;
    private List<MapContributor> contributors;
    private Region region;

    private boolean loaded;
    private MapLoader loader;

    public PlayingMap(final MapLoader loader) {
        this.loader = Validate.notNull(loader);
    }

    public String getName() {
        return name;
    }

    public List<MapContributor> getContributors() {
        return contributors;
    }

    public Region getRegion() {
        return region;
    }

    public boolean isOccupied() {
        return false;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public MapLoader getLoader() {
        return loader;
    }

    public void validate() throws MapLoaderException {
        if (name == null)
            throw new MapLoaderException("Name is undefined");

        if (objectives == null)
            throw new MapLoaderException("Objectives are undefined for map %s", name);

        if (objectives.isEmpty())
            throw new MapLoaderException("There are no objectives for map %s", name);

        if (contributors == null)
            throw new MapLoaderException("Contributors are undefined for map %s", name);

        if (contributors.isEmpty())
            throw new MapLoaderException("There are no contributors for map %s", name);

        if (region == null)
            throw new MapLoaderException("Region is undefined for map %s", name);

        if (!Regions.getManager().isRegistered(region))
            throw new MapLoaderException("Regions has not been registered for map %s", name);

        Core.debug("Validation for map %s successful", name);
    }

    public List<MapObjective> getObjectives() {
        return objectives;
    }
}