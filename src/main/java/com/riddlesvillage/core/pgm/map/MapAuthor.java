/*
 * rv_core
 * 
 * Created on 06 July 2017 at 1:07 PM.
 */

package com.riddlesvillage.core.pgm.map;

import java.util.Optional;

public final class MapAuthor extends MapContributor {

    public MapAuthor(String name) {
        super(name, Optional.of("Creator of the map"));
    }
}