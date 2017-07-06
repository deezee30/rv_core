/*
 * rv_core
 * 
 * Created on 06 July 2017 at 12:24 PM.
 */

package com.riddlesvillage.core.pgm.loader;

import com.riddlesvillage.core.pgm.map.PlayingMap;

import java.io.File;
import java.util.Optional;

public interface MapLoader {

    Optional<PlayingMap> load(final File file);

    boolean unload(final PlayingMap map);
}