/*
 * rv_core
 * 
 * Created on 16 July 2017 at 11:53 PM.
 */

package com.riddlesvillage.core.world.schematic.codec;

import com.riddlesvillage.core.world.schematic.Schematic;
import org.bukkit.World;

import java.io.File;

@FunctionalInterface
public interface SchematicEncoder {

    File encode(final Schematic schematic,
                final World world) throws SchematicCodecException;
}