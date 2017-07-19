/*
 * rv_core
 * 
 * Created on 16 July 2017 at 11:51 PM.
 */

package com.riddlesvillage.core.world.schematic.codec;

import com.riddlesvillage.core.world.schematic.SchematicData;

import java.io.File;

@FunctionalInterface
public interface SchematicDecoder {

    SchematicData decode(final File file) throws SchematicCodecException;
}