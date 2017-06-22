/*
 * MaulssLib
 * 
 * Created on 10 February 2015 at 4:22 PM.
 */

package com.riddlesvillage.core.world.region;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.world.Vector3D;
import com.riddlesvillage.core.world.region.type.RegionType;
import com.riddlesvillage.core.world.region.type.RegionTypeAdapter;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;

import java.util.Optional;

public final class Regions {

	public static final Gson REGION_GSON = new GsonBuilder()
			.disableInnerClassSerialization()
			.setPrettyPrinting()
			.registerTypeAdapter(Region.class, RegionTypeAdapter.getInstance())
			.create();

	public static final String TYPE_META = RegionTypeAdapter.META_TYPE;

	private Regions() {}

	/**
	 * Gets all regions that contain the given point.
	 *
	 * @param   point
	 * 			Point to check against
	 * @return  Regions which contain this point
	 * @see 	Region#contains(Vector3D)
	 */
	public static synchronized RegionList getContaining(Vector3D point,
														Region... regions) {
		Validate.notNull(point);
		Validate.notNull(regions);
		Validate.notEmpty(regions);
		Validate.noNullElements(regions);

		RegionList contain = new RegionList(regions.length);

		for (Region region : regions)
			contain.addIf(region.contains(point), region);

		return contain;
	}

	public static synchronized Optional<Region> getPrioritized(Region region1, Region region2) {
		int c = region1.compareTo(region2);

		return c == 0 ? Optional.empty() : Optional.of(c < 0 ? region1 : region2);
	}

	public static synchronized ImmutableList<Region> getRegions(RegionCriteria criteria) {
		return getManager().getRegions(criteria);
	}

	public static synchronized ImmutableList<Region> getRegions() {
		return getManager().getRegions();
	}

	public static synchronized void validateRegion(Region region) throws RegionException {
		if (region.getType().equals(RegionType.CUSTOM))
			throw new RegionException("Region type can not be CUSTOM");

		if (Bukkit.getServer().getPluginManager().isPluginEnabled(Core.get())
				&& Bukkit.getWorld(region.getWorld()) == null)
			throw new RegionException("World %s doesn't exist", region.getWorld());

		if (getRegions().contains(region))
			throw new RegionException("Region already registered");

		// TODO: Perform more checks
	}

	@Beta
	public static synchronized Region fromJson(String json) {
		Region reg = REGION_GSON.fromJson(json, Region.type());
		reg.init();
		return reg;
	}

	public static synchronized RegionManager getManager() {
		return RegionManager.INSTANCE;
	}
}