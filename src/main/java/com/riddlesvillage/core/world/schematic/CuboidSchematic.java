/*
 * rv_core
 * 
 * Created on 16 July 2017 at 2:58 AM.
 */

package com.riddlesvillage.core.world.schematic;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.world.Vector3D;
import com.riddlesvillage.core.world.schematic.codec.SchematicCodecException;
import com.riddlesvillage.core.world.schematic.codec.SchematicCodecFactory;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.io.File;
import java.io.FileNotFoundException;

public class CuboidSchematic extends AbstractSchematic {

    private final SchematicData data;

    public CuboidSchematic(final String name,
                           final SchematicType type,
                           final SchematicData data) {
        super(name, type);
        this.data = Validate.notNull(data);
        Core.debug("Generated schematic " + getFullName());
    }

    public CuboidSchematic(final File file) throws FileNotFoundException, SchematicCodecException {
        super(file);
        data = SchematicCodecFactory.getFactory()
                .getCodec(type)
                .decode(file);
        Core.debug("Loaded schematic " + getFullName());
    }

    public CuboidSchematic(final String name) throws FileNotFoundException, SchematicCodecException {
        this(new File(Schematics.getSchematicFromDefaultLocation(name)));
    }

    @Override
    public SchematicData getData() {
        return data;
    }

    @Override
    public boolean paste(final Location baseLocation) {
        Validate.notNull(baseLocation);

        short[] blocks = data.getBlocks();
        byte[] blockData = data.getBlockData();

        Vector3D size = data.getSize();
        int width = size.getFloorX();
        int height = size.getFloorY();
        int length = size.getFloorZ();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    Block block = new Location(
                            baseLocation.getWorld(),
                            x + baseLocation.getX(),
                            y + baseLocation.getY(),
                            z + baseLocation.getZ()
                    ).getBlock();
                    block.setTypeIdAndData(blocks[index], blockData[index], true);
                    Core.debug("Block pasted at %s", block.getLocation());

                    // TODO: Update block's NBT data to add tile entities
                }
            }
        }

        Core.debug("Pasted %s @ %s", getFullName(), Vector3D.fromLocation(baseLocation));

        return true;
    }
}