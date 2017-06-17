/*
 * rv_core
 * 
 * Created on 15 June 2017 at 12:13 PM.
 */

package com.riddlesvillage.core.world.region;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.riddlesvillage.core.collect.EnhancedList;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.json.simple.JSONAware;

import java.util.Collection;
import java.util.Map;

public final class FlagList extends EnhancedList<Flag> implements JSONAware, ConfigurationSerializable {

	public FlagList() {
	}

	public FlagList(int initialCapacity) {
		super(initialCapacity);
	}

	public FlagList(Flag... elements) {
		super(elements);
	}

	public FlagList(Collection<? extends Flag> c) {
		super(c);
	}

	@Override
	public Map<String, Object> serialize() {
		return ImmutableMap.<String, Object>builder()
				.put("flags", this)
				.build();
	}

	public JsonArray toJsonArray() {
		JsonArray jsonArray = new JsonArray();

		for (Flag flag : this) {
			jsonArray.add(new JsonPrimitive(flag.getFlag()));
		}

		return jsonArray;
	}

	@Override
	public String toJSONString() {
		return toJsonArray().toString();
	}

	@Override
	public String toString() {
		return toJSONString();
	}

	public static FlagList from(Iterable<String> flags) {
		FlagList fl = new FlagList();
		for (String flag : flags) {
			fl.add(Flag.from(flag));
		}

		return fl;
	}

	public static FlagList from(String json) {
		return from(new JsonParser().parse(json).getAsJsonArray());
	}

	public static FlagList from(JsonArray array) {
		FlagList flags = new FlagList();

		for (JsonElement jelement : array) {
			flags.add(Flag.from(jelement.getAsString()));
		}

		return flags;
	}

	public static FlagList deserialize(Map<String, Object> data) {
		return new FlagList();
	}
}