/*
 * rv_core
 * 
 * Created on 03 June 2017 at 10:17 PM.
 */

package com.riddlesvillage.core.database;

import java.util.Map;

public interface StatType {

	String getStat();

	Object getDefault();

	default Map<String, Object> append(Map<String, Object> map) {
		return append(map, getDefault());
	}

	default Map<String, Object> append(Map<String, Object> map, Object def) {
		map.put(getStat(), def);
		return map;
	}

	static StatType create(String stat) {
		return create(stat, null);
	}

	static StatType create(String stat, Object def) {
		return new StatType() {

			@Override
			public String getStat() {
				return stat;
			}

			@Override
			public Object getDefault() {
				return def;
			}

			@Override
			public String toString() {
				return stat;
			}
		};
	}
}
