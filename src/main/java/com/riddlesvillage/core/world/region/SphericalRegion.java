/*
 * MaulssLib
 * 
 * Created on 07 February 2015 at 9:47 PM.
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

public class SphericalRegion extends Region {

	private static final long serialVersionUID = -5576209688378810728L;

	private final Vector3D
			center,
			minBounds,
			maxBounds;
	private final int
			radius,
			volume;

	public SphericalRegion(World world,
						   Vector3D center,
						   int radius) {
		super(world);
		this.center = Validate.notNull(center, "The center point can not be null");
		this.radius = radius = Math.abs(radius);

		volume    = MathUtil.round(4 * Math.PI * Math.pow(radius, 3) / 3);
		minBounds = center.clone().subtract(radius);
		maxBounds = center.clone().add(radius);
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
	public RegionType getRegionType() {
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
				.put("type", getRegionType())
				.put("world", getWorld().getName())
				.put("center", center)
				.put("radius", radius)
				.build();
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject json = new JsonObject();

		json.addProperty("type", getRegionType().name());
		json.addProperty("world", getWorld().getName());
		json.add("center", center.toJsonObject());
		json.addProperty("radius", radius);

		return json;
	}
}