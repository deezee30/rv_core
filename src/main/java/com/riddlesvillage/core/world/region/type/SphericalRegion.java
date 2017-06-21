/*
 * MaulssLib
 * 
 * Created on 07 February 2015 at 9:47 PM.
 */

package com.riddlesvillage.core.world.region.type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.util.MathUtil;
import com.riddlesvillage.core.world.Vector3D;
import com.riddlesvillage.core.world.region.Region;
import com.riddlesvillage.core.world.region.RegionBoundsException;
import com.riddlesvillage.core.world.region.Regions;
import org.apache.commons.lang3.Validate;

import java.util.Map;

public class SphericalRegion extends Region {

	private static final long serialVersionUID = -5576209688378810728L;

	private final Vector3D center;
	private final int radius;

	// do not serialize these
	private transient int volume;
	private transient Vector3D minBounds, maxBounds;

	public SphericalRegion(String world,
						   Vector3D center,
						   int radius) {
		super(world);
		this.center = Validate.notNull(center, "The center point can not be null");
		this.radius = radius = Math.abs(radius);

		init();
	}

	@Override
	public void calculate() {
		volume		= MathUtil.round(4 * Math.PI * Math.pow(radius, 3) / 3);
		minBounds	= center.clone().subtract(radius);
		maxBounds	= center.clone().add(radius);
	}

	@Override
	public int getVolume() {
		return volume;
	}

	@Override
	public Vector3D getMinBounds() {
		return minBounds;
	}

	@Override
	public Vector3D getMaxBounds() {
		return maxBounds;
	}

	@Override
	public boolean contains(Vector3D vector) {
		return center.distanceSquared(vector) <= Math.pow(radius, 2);
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
		throw new UnsupportedOperationException();
	}

	@Override
	public RegionType getType() {
		return RegionType.SPHERICAL;
	}

	@Override
	public Region joinWith(Region other) throws RegionBoundsException {
		throw new UnsupportedOperationException();
	}

	public final Vector3D getCenter() {
		return center;
	}

	public final double getRadius() {
		return radius;
	}

	@Override
	public Map<String, Object> serialize() {
		return ImmutableMap.<String, Object>builder()
				.put(Regions.TYPE_META, getType())
				.put("world", getWorld())
				.put("center", center)
				.put("radius", radius)
				.build();
	}
}