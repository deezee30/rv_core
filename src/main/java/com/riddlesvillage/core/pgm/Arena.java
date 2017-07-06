/*
 * rv_core
 * 
 * Created on 06 July 2017 at 1:20 PM.
 */

package com.riddlesvillage.core.pgm;

import com.riddlesvillage.core.pgm.map.PlayingMap;
import org.apache.commons.lang3.Validate;

public class Arena {

    private final PlayingMap map;

    public Arena(final PlayingMap map) {
        this.map = Validate.notNull(map);
    }

    public PlayingMap getMap() {
        return map;
    }
}