/*
 * MaulssLib
 * 
 * Created on 10 February 2015 at 6:57 PM.
 */

package com.riddlesvillage.core.world.region.type;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.riddlesvillage.core.Core;
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

@Beta
public class CylindricalRegion extends Region {

    private static final long serialVersionUID = 2750518492398124058L;

    private final Vector3D base;
    private final int radius, height;

    // do not serialize these
    private transient Vector3DList points;
    private transient Vector3D min, max;
    private transient int volume;

    public CylindricalRegion(String world,
                             Vector3D base,
                             int radius,
                             int height) {
        super(world);
        this.base = Validate.notNull(base, "The base point can not be null").floor();
        this.radius = Math.abs(radius);
        this.height = Math.abs(height);

        calculate();
    }

    @Override
    public void calculate() {
        // TODO: calculate points
        points = new Vector3DList();

        // find points in region


        // calculate dimensions
        volume = MathUtil.round(Math.PI * Math.pow(radius, 2) * height);

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

        Core.debug("CYLINDER: Measured volume: %s; Calculated volume: %s", points.size(), volume);
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
        return vector.getY() >= base.getY()
                && vector.getY() <= base.getY() + height
                && Math.pow(vector.getX() - base.getX(), 2) + Math.pow(vector.getZ() - base.getZ(), 2) < Math.pow(radius, 2);
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
        return RegionType.CYLINDRICAL;
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