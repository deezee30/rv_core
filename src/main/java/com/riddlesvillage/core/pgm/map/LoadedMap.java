/*
 * rv_core
 * 
 * Created on 06 July 2017 at 1:01 PM.
 */

package com.riddlesvillage.core.pgm.map;

import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.collect.EnhancedMap;
import com.riddlesvillage.core.pgm.loader.MapLoader;
import com.riddlesvillage.core.pgm.team.Team;
import com.riddlesvillage.core.world.Vector3D;
import org.apache.commons.lang3.Validate;

import java.util.List;

public abstract class LoadedMap {

    private String name;
    private EnhancedList<MapObjective> objectives = new EnhancedList<>();
    private EnhancedList<MapContributor> contributors = new EnhancedList<>();
    private EnhancedMap<Team, List<Vector3D>> spawns = new EnhancedMap<>();

    private boolean occupied = false;
    private boolean loaded = false;
    private MapLoader loader;

    public LoadedMap(final MapLoader loader) {
        this.loader = Validate.notNull(loader);
    }

    public String getName() {
        return name;
    }

    public List<MapObjective> getObjectives() {
        return objectives;
    }

    public List<MapContributor> getContributors() {
        return contributors;
    }

    public EnhancedMap<Team, List<Vector3D>> getSpawns() {
        return spawns;
    }

    public boolean isOccupied() {
        return loaded && occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public MapLoader getLoader() {
        return loader;
    }
}