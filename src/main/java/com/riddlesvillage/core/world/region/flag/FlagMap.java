/*
 * rv_core
 * 
 * Created on 21 June 2017 at 12:43 AM.
 */

package com.riddlesvillage.core.world.region.flag;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.riddlesvillage.core.collect.EnhancedMap;
import com.riddlesvillage.core.world.region.Regions;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class FlagMap extends EnhancedMap<Flag, Boolean> implements ConfigurationSerializable {

	public FlagMap() {}

	public FlagMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public FlagMap(int initialCapacity) {
		super(initialCapacity);
	}

	public FlagMap(Map<? extends Flag, Boolean> m) {
		super(m);
	}

	public FlagMap(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}

	public boolean isAllowed(Flag flag) {
		Boolean val = get(flag);
		return val == null ? true : val;
	}

	@Override
	public Gson getGson() {
		return Regions.REGION_GSON;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = Maps.newHashMap();
		for (Map.Entry<Flag, Boolean> entry : entrySet())
			map.put(entry.getKey().flag, entry.getValue());
		return map;
	}

	public static FlagMap deserialize(Map<String, Object> data) {
		FlagMap map = new FlagMap();
		for (Map.Entry<String, Object> entry : data.entrySet())
			map.put(Flag.from(entry.getKey()), (boolean) entry.getValue());
		return map;
	}

	public static FlagMap fromJson(String json) {
		return Regions.REGION_GSON.fromJson(json, FlagMap.class);
	}
}