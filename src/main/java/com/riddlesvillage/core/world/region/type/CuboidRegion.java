/*
 * MaulssLib
 * 
 * Created on 07 February 2015 at 8:24 PM.
 */

package com.riddlesvillage.core.world.region.type;

import com.google.common.collect.ImmutableList;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.collect.EnhancedMap;
import com.riddlesvillage.core.util.MathUtil;
import com.riddlesvillage.core.world.Vector3D;
import com.riddlesvillage.core.world.Vector3DList;
import com.riddlesvillage.core.world.region.Region;
import com.riddlesvillage.core.world.region.RegionBoundsException;
import com.riddlesvillage.core.world.region.Regions;
import org.apache.commons.lang3.Validate;

import java.util.Map;

public class CuboidRegion extends Region {

    private static final long serialVersionUID = -1670751903460496963L;

    private final Vector3D min, max;

    // do not serialize these
    private transient Vector3DList points;
    private transient int volume, width, height, depth;

    public CuboidRegion(final String world,
                        final Vector3D min,
                        final Vector3D max) {
        super(world);

		this.min = Validate.notNull(min, "The min point can not be null").floor();
		this.max = Validate.notNull(max, "The max point can not be null").floor();

        init();
    }

    @Override
    public void calculate() {
        points = new Vector3DList();

        // find points in region
        for (int x = (int) Math.min(min.getX(), max.getX()); x <= Math.max(min.getX(), max.getX()); x++)
            for (int y = (int) Math.min(min.getY(), max.getY()); y <= Math.max(min.getY(), max.getY()); y++)
                for (int z = (int) Math.min(min.getZ(), max.getZ()); z <= Math.max(min.getZ(), max.getZ()); z++)
                    points.add(new Vector3D(x, y, z));

        // calculate dimensions
        width	= MathUtil.floor(max.getX() - min.getX() + 1);
        height	= MathUtil.floor(max.getY() - min.getY() + 1);
        depth	= MathUtil.floor(max.getZ() - min.getZ() + 1);
        volume	= width * height * depth;

        Messaging.debug("CUBOID: Measured volume: %s; Calculated volume: %s", points.size(), volume);
    }

    @Override
    public Vector3D getMin() {
        return min;
    }

    @Override
    public Vector3D getMax() {
        return max;
    }

    @Override
    public int getVolume() {
        return volume;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public boolean contains(Vector3D vector) {
        return vector.getX() >= minX
                && vector.getX() <= maxX
                && vector.getY() >= minY
                && vector.getY() <= maxY
                && vector.getZ() >= minZ
                && vector.getZ() <= maxZ;
    }

    @Override
    public EnhancedList<Vector3D> getWalls() {
        EnhancedList<Vector3D> points = new EnhancedList<>();

        loop(vector -> {
            int x = vector.getFloorX();
            int y = vector.getFloorY();
            int z = vector.getFloorZ();

            if (x == minX || x == maxX ||
                    y == minY || y == maxY ||
                    z == minZ || z == maxZ) {
                points.add(vector);
            }
        });

        return points;
    }

    @Override
    public EnhancedList<Vector3D> getEdges() {
        EnhancedList<Vector3D> points = new EnhancedList<>();

        loop(vector -> {
            int x = vector.getFloorX();
            int y = vector.getFloorY();
            int z = vector.getFloorZ();

            boolean edge = false;

            if ((x == minX || x == maxX) && (y == minY || y == maxY)) edge = true;
            if ((z == minZ || z == maxZ) && (y == minY || y == maxY)) edge = true;
            if ((x == minX || x == maxX) && (z == minZ || z == maxZ)) edge = true;

            points.addIf(edge, vector);
        });

        return points;
    }

    @Override
    public ImmutableList<Vector3D> getPoints() {
        return points.getImmutableElements();
    }

    @Override
    public RegionType getType() {
        return RegionType.CUBOID;
    }

    @Override
    public Region joinWith(Region other) throws RegionBoundsException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> serialize() {
        EnhancedMap<String, Object> map = new EnhancedMap<>();
        map.put(Regions.TYPE_META, getType());
        map.put("world", getWorld());
        map.put("min", min);
        map.put("max", max);
        map.putIf(hasPriority(), "priority", getPriority().get());
        return map.getImmutableEntries();
    }
}