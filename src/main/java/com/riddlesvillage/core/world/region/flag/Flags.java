/*
 * rv_core
 * 
 * Created on 20 June 2017 at 11:58 PM.
 */

package com.riddlesvillage.core.world.region.flag;

import com.riddlesvillage.core.collect.EnhancedMap;

import java.util.List;

public final class Flags {

	private Flags() {}

	public static synchronized EnhancedMap<String, Boolean> toMap(List<Flag> flags) {
		EnhancedMap<String, Boolean> map = new EnhancedMap<>();

		for (Flag flag : flags) {
			map.put(flag.flag, flag.isAllowed());
		}

		return map;
	}
}