/*
 * MaulssLib
 * 
 * Created on 07 February 2015 at 8:24 PM.
 */

package com.riddlesvillage.core.world.region.type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.util.MathUtil;
import com.riddlesvillage.core.world.Vector3D;
import com.riddlesvillage.core.world.region.Region;
import com.riddlesvillage.core.world.region.RegionBoundsException;
import com.riddlesvillage.core.world.region.Regions;
import org.apache.commons.lang3.Validate;

import java.util.Map;

public class CuboidRegion extends Region {

	private static final long serialVersionUID = -1670751903460496963L;

	private final Vector3D min, max;

	// do not serialize these
	private transient EnhancedList<Vector3D> points;
	private transient int volume, width, height, depth;

	public CuboidRegion(String world,
						Vector3D min,
						Vector3D max) {
		super(world);

		this.min = Validate.notNull(min, "The min point can not be null").floor();
		this.max = Validate.notNull(max, "The max point can not be null").floor();

		init();
	}

	@Override
	public void init() {
		points = new EnhancedList<>();

		for (int x = (int) Math.min(min.getX(), max.getX()); x <= Math.max(min.getX(), max.getX()); x++)
			for (int y = (int) Math.min(min.getY(), max.getY()); y <= Math.max(min.getY(), max.getY()); y++)
				for (int z = (int) Math.min(min.getZ(), max.getZ()); z <= Math.max(min.getZ(), max.getZ()); z++) {
					Vector3D v = new Vector3D(x, y, z);
					points.add(v);
					Messaging.debug(v.toString());
				}

		width	= MathUtil.floor(max.getX() - min.getX() + 1);
		height	= MathUtil.floor(max.getY() - min.getY() + 1);
		depth	= MathUtil.floor(max.getZ() - min.getZ() + 1);
		volume	= width * height * depth;
	}

	@Override
	public int getVolume() {
		return volume;
	}

	@Override
	public Vector3D getMinBounds() {
		return min;
	}

	@Override
	public Vector3D getMaxBounds() {
		return max;
	}

	@Override
	public boolean contains(Vector3D vector) {
		return Validate.notNull(vector).getX() >= min.getX()
				&& vector.getX() <= max.getX()
				&& vector.getY() >= min.getY()
				&& vector.getY() <= max.getY()
				&& vector.getZ() >= min.getZ()
				&& vector.getZ() <= max.getZ();
	}

	@Override
	public EnhancedList<Vector3D> getWalls() {
		EnhancedList<Vector3D> points = new EnhancedList<>();

		loop(vector -> {
			int x = vector.getFloorX();
			int y = vector.getFloorY();
			int z = vector.getFloorZ();

			if (x == getMinX() || x == getMaxX() ||
					y == getMinY() || y == getMaxY() ||
					z == getMinZ() || z == getMaxZ()) {
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

			if ((x == getMinX() || x == getMaxX()) &&
					(y == getMinY() || y == getMaxY())) edge = true;
			if ((z == getMinZ() || z == getMaxZ()) &&
					(y == getMinY() || y == getMaxY())) edge = true;
			if ((x == getMinX() || x == getMaxX()) &&
					(z == getMinZ() || z == getMaxZ())) edge = true;

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

	/**
	 * @return width of the cuboid (x-direction)
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return height of the cuboid (y-direction)
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return depth of the cuboid (z-direction)
	 */
	public int getDepth() {
		return depth;
	}

	@Override
	public Map<String, Object> serialize() {
		return ImmutableMap.<String, Object>builder()
				.put(Regions.TYPE_META, getType())
				.put("world", getWorld())
				.put("min", min)
				.put("max", max)
				.build();
	}
}