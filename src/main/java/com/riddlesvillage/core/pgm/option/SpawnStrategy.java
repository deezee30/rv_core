/*
 * rv_core
 * 
 * Created on 10 July 2017 at 3:23 AM.
 */

package com.riddlesvillage.core.pgm.option;

import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.pgm.SpawnReason;
import com.riddlesvillage.core.pgm.player.GamePlayer;
import org.bukkit.Location;

@FunctionalInterface
public interface SpawnStrategy {

    Location findSpawn(final GamePlayer player,
                       final EnhancedList<Location> possibleSpawns,
                       final SpawnReason reason);
}