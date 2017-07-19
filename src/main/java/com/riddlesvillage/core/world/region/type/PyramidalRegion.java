/*
 * rv_core
 * 
 * Created on 22 June 2017 at 1:18 AM.
 */

package com.riddlesvillage.core.world.region.type;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.collect.EnhancedMap;
import com.riddlesvillage.core.world.Vector3D;
import com.riddlesvillage.core.world.Vector3DList;
import com.riddlesvillage.core.world.region.Region;
import com.riddlesvillage.core.world.region.RegionBoundsException;
import com.riddlesvillage.core.world.region.Regions;
import org.apache.commons.lang3.Validate;

import java.util.Map;

@Beta
public class PyramidalRegion extends Region {

    private static final long serialVersionUID = 8844953850046967679L;

    private final Vector3D base;
    private int radius, height;

    // do not serialize these
    private transient Vector3DList points;
    private transient Vector3D min, max;
    private transient int volume;

    protected PyramidalRegion(String world,
                              Vector3D base,
                              int radius) {
        super(world);
        this.base = Validate.notNull(base, "The base point can not be null").floor();

        calculate();
    }

    protected PyramidalRegion(String world,
                              Vector3D base,
                              Vector3D peak) {
        super(world);
        this.base = Validate.notNull(base, "The base point can not be null").floor();

        calculate();
    }

    @Override
    public void calculate() {
        points = new Vector3DList();

        // TODO: find points in region


        // TODO: calculate dimensions


        min = new Vector3D(
                base.getX() - radius,
                base.getY(),
                base.getZ() - radius
        );

        max = new Vector3D(
                base.getX() + radius,
                base.getY() + height,
                base.getZ() + radius
        );

        Core.debug("PYRAMID: Measured volume: %s; Calculated volume: %s", points.size(), volume);
    }

    public Vector3D getBase() {
        return base;
    }

    public int getRadius() {
        return radius;
    }

    public int getHeight() {
        return height;
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

    @Override
    public boolean contains(Vector3D vector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EnhancedList<Vector3D> getWalls() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EnhancedList<Vector3D> getEdges() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableList<Vector3D> getPoints() {
        return points.getImmutableElements();
    }

    @Override
    public RegionType getType() {
        return RegionType.PYRAMID;
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
        map.put("base", base);
        map.put("radius", radius);
        map.put("height", height);
        map.putIf(hasPriority(), "priority", getPriority().get());
        return map.getImmutableEntries();
    }
}