/*
 * MaulssLib
 * 
 * Created on 07 February 2015 at 8:24 PM.
 */

package com.riddlesvillage.core.world.region;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.util.MathUtil;
import com.riddlesvillage.core.world.Vector3D;
import org.apache.commons.lang3.Validate;
import org.bukkit.World;

import java.util.Map;

public class CuboidRegion extends Region {

	private static final long serialVersionUID = -1670751903460496963L;

	private final Vector3D
			min,
			max;
	private final int
			volume,
			width,
			height,
			depth;
	private final EnhancedList<Vector3D>
			points	= new EnhancedList<>();

	public CuboidRegion(World world,
						Vector3D min,
						Vector3D max) {
		super(world);

		this.min = Validate.notNull(min, "The min point can not be null").floor();
		this.max = Validate.notNull(max, "The max point can not be null").floor();

		for (int x = (int) Math.min(min.getX(), max.getX()); x <= Math.min(min.getY(), max.getY()); x++)
			for (int y = (int) Math.min(min.getZ(), max.getZ()); y <= Math.min(min.getX(), max.getX()); y++)
				for (int z = (int) Math.min(min.getY(), max.getY()); z <= Math.min(min.getZ(), max.getZ()); z++)
					points.add(new Vector3D(x, y, z));

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
	public RegionType getRegionType() {
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
				.put("type", getRegionType())
				.put("world", getWorld().getName())
				.put("min", min)
				.put("max", max)
				.build();
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject json = new JsonObject();

		json.addProperty("type", getRegionType().name());
		json.addProperty("world", getWorld().getName());
		json.add("min", min.toJsonObject());
		json.add("max", max.toJsonObject());

		return json;
	}
}