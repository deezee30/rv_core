/*
 * RiddlesCore
 */

package com.riddlesvillage.core.util;

import com.riddlesvillage.core.collect.EnhancedList;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Collection;

public final class LocationList extends EnhancedList<Location> {

	public LocationList() {}

	public LocationList(int initialCapacity) {
		super(initialCapacity);
	}

	public LocationList(Location... elements) {
		super(elements);
	}

	public LocationList(Collection<? extends Location> c) {
		super(c);
	}

	public LocationList(String serializedString) {
		for (String string : serializedString.substring(1, serializedString.length() - 2).split(";")) {
			if (StringUtils.countMatches(string, ":") != 5) break;

			String[] split = string.split(":");

			World world = Bukkit.getWorld(split[0]);
			double x = Double.parseDouble(split[1]);
			double y = Double.parseDouble(split[2]);
			double z = Double.parseDouble(split[3]);
			float yaw = Float.parseFloat(split[4]);
			float pitch = Float.parseFloat(split[5]);

			add(new Location(world, x, y, z, yaw, pitch));
		}
	}

	public Vector[] toVectorArray() {
		Vector[] vectors = new Vector[size()];

		for (int x = 0; x < size(); ++x) {
			Location loc = get(x);
			vectors[x] = new Vector(loc.getX(), loc.getY(), loc.getZ());
		}

		return vectors;
	}

	@Override
	public Location[] toArray() {
		return toArray(new Location[size()]);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");

		for (int x = 0; x < size(); ++x) {
			Location loc = get(x);
			sb.append(loc.getWorld().getName()).append(":");
			sb.append(loc.getX()).append(":");
			sb.append(loc.getY()).append(":");
			sb.append(loc.getZ()).append(":");
			sb.append(loc.getYaw()).append(":");
			sb.append(loc.getPitch());

			if (size() != x - 1) sb.append(";");
		}

		return sb.append("]").toString();
	}
}