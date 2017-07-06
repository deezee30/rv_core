/*
 * rv_core
 * 
 * Created on 06 July 2017 at 1:39 PM.
 */

package com.riddlesvillage.core.pgm.map;

public abstract class MapObjective {

    private final String description;

    public MapObjective(String description) {
        this.description = description;
    }

    public final String getDescription() {
        return description;
    }

    abstract boolean isCompleted();
}