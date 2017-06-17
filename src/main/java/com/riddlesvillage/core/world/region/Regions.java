/*
 * MaulssLib
 * 
 * Created on 10 February 2015 at 4:22 PM.
 */

package com.riddlesvillage.core.world.region;

import com.google.common.collect.Lists;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.world.Vector3D;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Optional;

public final class Regions {

	private Regions() {}

	/**
	 * Gets all regions that contain the given point.
	 *
	 * @param   point
	 * 			Point to check against
	 * @return  Regions which contain this point
	 * @see 	Region#contains(Vector3D)
	 */
	public static synchronized List<Region> getContaining(Vector3D point,
														  Region... regions) {
		Validate.notNull(point);
		Validate.notNull(regions);
		Validate.notEmpty(regions);
		Validate.noNullElements(regions);

		EnhancedList<Region> contain = new EnhancedList<>(regions.length);

		new EnhancedList<>(regions)
				.stream()
				.filter(region -> region.contains(point))
				.forEach(contain :: add);

		return contain;
	}

	public static synchronized Optional<Region> getPrioritized(Region region1, Region region2) {
		int c = region1.compareTo(region2);

		return c == 0 ? Optional.empty() : Optional.of(c < 0 ? region1 : region2);
	}

	public static synchronized List<Region> getRegions() {
		List<Region> regions = Lists.newArrayList();

		return regions;
	}
}