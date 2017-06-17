/*
 * MaulssLib
 * 
 * Created on 10 February 2015 at 6:57 PM.
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

public class CylindricalRegion extends Region {

	private static final long serialVersionUID = 2750518492398124058L;

	private final Vector3D
			base,
			minBounds,
			maxBounds;
	private final int
			radius,
			height,
			volume;

	public CylindricalRegion(World world,
							 Vector3D base,
							 int radius,
							 int height) {
		super(world);
		this.base = Validate.notNull(base, "The base point can not be null");
		this.radius = radius = Math.abs(radius);
		this.height = height = Math.abs(height);

		volume = MathUtil.round(Math.PI * Math.pow(radius, 2) * height);

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
		return Validate.notNull(vector).getY() >= base.getY()
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
		throw new UnsupportedOperationException();
	}

	@Override
	public RegionType getRegionType() {
		return RegionType.CYLINDRICAL;
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
				.put("type", getRegionType())
				.put("world", getWorld().getName())
				.put("base_center", base)
				.put("radius", radius)
				.put("height", height)
				.build();
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject json = new JsonObject();

		json.addProperty("type", getRegionType().name());
		json.addProperty("world", getWorld().getName());
		json.add("base_center", base.toJsonObject());
		json.addProperty("radius", radius);
		json.addProperty("height", height);

		return json;
	}
}