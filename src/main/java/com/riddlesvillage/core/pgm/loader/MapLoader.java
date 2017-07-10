/*
 * rv_core
 * 
 * Created on 06 July 2017 at 12:24 PM.
 */

package com.riddlesvillage.core.pgm.loader;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.pgm.map.LoadedMap;

import java.io.InputStream;
import java.util.Optional;

public interface MapLoader {

    Optional<LoadedMap> load(final InputStream input);

    boolean unload(final LoadedMap map);

    default void validate(LoadedMap map) throws MapLoaderException {
        String name = map.getName();

        if (name == null)
            throw new MapLoaderException("Name is undefined");

        if (map.getObjectives() == null)
            throw new MapLoaderException("Objectives are undefined for map %s", name);

        if (map.getObjectives().isEmpty())
            throw new MapLoaderException("There are no objectives for map %s", name);

        if (map.getContributors() == null)
            throw new MapLoaderException("Contributors are undefined for map %s", name);

        if (map.getContributors().isEmpty())
            throw new MapLoaderException("There are no contributors for map %s", name);

        Core.debug("Validation for map %s successful", name);
    }
}