/*
 * rv_core
 * 
 * Created on 03 June 2017 at 10:17 PM.
 */

package com.riddlesvillage.core.database;

import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.Optional;

/**
 * The interface Stat type.
 */
public interface StatType {

    /**
     * Gets stat.
     *
     * @return the stat
     */
    String getStat();

    /**
     * Gets default.
     *
     * @return the default
     */
    Optional<Object> getDefault();

    /**
     * Append map.
     *
     * @param map the map
     * @return the map
     */
    default Map<String, Object> append(final Map<String, Object> map) {
        Validate.notNull(map);
        return append(map, getDefault().isPresent() ? getDefault().get() : null);
    }

    /**
     * Append map.
     *
     * @param map the map
     * @param def the def
     * @return the map
     */
    default Map<String, Object> append(final Map<String, Object> map,
                                       final Object def) {
        Validate.notNull(map);

        map.put(getStat(), def);
        return map;
    }

    /**
     * Create stat type.
     *
     * @param stat the stat
     * @return the stat type
     */
    static StatType create(final String stat) {
        return create(stat, null);
    }

    /**
     * Create stat type.
     *
     * @param stat the stat
     * @param def  the def
     * @return the stat type
     */
    static StatType create(final String stat,
                           final Object def) {
        Validate.notNull(stat);

        return new StatType() {

            @Override
            public String getStat() {
                return stat;
            }

            @Override
            public Optional<Object> getDefault() {
                return Optional.ofNullable(def);
            }

            @Override
            public String toString() {
                return stat;
            }
        };
    }
}
