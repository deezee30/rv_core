/*
 * rv_core
 * 
 * Created on 19 June 2017 at 12:47 PM.
 */

package com.riddlesvillage.core.world.region;

import com.google.gson.Gson;
import com.riddlesvillage.core.collect.EnhancedList;

import java.util.Collection;

public class RegionList extends EnhancedList<Region> {

	public RegionList() {}

	public RegionList(int initialCapacity) {
		super(initialCapacity);
	}

	public RegionList(Region... elements) {
		super(elements);
	}

	public RegionList(Collection<? extends Region> c) {
		super(c);
	}

	@Override
	public Gson getGson() {
		return Regions.REGION_GSON;
	}

	public static RegionList fromJson(String json) {
		RegionList list = Regions.REGION_GSON.fromJson(json, RegionList.class);
		list.forEach(Region::init);
		return list;
	}
}