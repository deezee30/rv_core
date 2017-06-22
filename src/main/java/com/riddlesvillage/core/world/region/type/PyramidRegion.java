/*
 * rv_core
 * 
 * Created on 22 June 2017 at 1:18 AM.
 */

package com.riddlesvillage.core.world.region.type;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.world.Vector3D;
import com.riddlesvillage.core.world.Vector3DList;
import com.riddlesvillage.core.world.region.Region;
import com.riddlesvillage.core.world.region.RegionBoundsException;
import com.riddlesvillage.core.world.region.Regions;
import org.apache.commons.lang3.Validate;

import java.util.Map;

@Beta
public class PyramidRegion extends Region {

	private static final long serialVersionUID = 1459749783605187047L;

	private final Vector3D base;
	private int radius, height;

	// do not serialize these
	private transient Vector3DList points;
	private transient Vector3D minBounds, maxBounds;
	private transient int volume;

	protected PyramidRegion(String world,
							Vector3D base,
							int radius) {
		super(world);
		this.base = Validate.notNull(base, "The base point can not be null").floor();

		init();
	}

	protected PyramidRegion(String world,
							Vector3D base,
							Vector3D peak) {
		super(world);
		this.base = Validate.notNull(base, "The base point can not be null").floor();

		init();
	}

	@Override
	public void calculate() {
		points = new Vector3DList();

		// TODO: find points in region


		// TODO: calculate dimensions


		minBounds = new Vector3D(
				base.getX() - radius,
				base.getY(),
				base.getZ() - radius
		);

		maxBounds = new Vector3D(
				base.getX() + radius,
				base.getY() + height,
				base.getZ() + radius
		);

		Messaging.debug("PYRAMID: Measured volume: %s; Calculated volume: %s", points.size(), volume);
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

	public final Vector3D getBaseCenter() {
		return base;
	}

	public final double getRadius() {
		return radius;
	}

	public final double getHeight() {
		return height;
	}

	@Override
	public Map<String, Object> serialize() {
		return ImmutableMap.<String, Object>builder()
				.put(Regions.TYPE_META, getType())
				.put("world", getWorld())
				.put("base_center", base)
				.put("radius", radius)
				.put("height", height)
				.build();
	}
}