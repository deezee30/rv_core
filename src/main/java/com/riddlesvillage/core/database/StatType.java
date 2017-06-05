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
}