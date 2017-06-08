/*
 * MaulssLib
 *
 * Created on 25 December 2014 at 5:05 PM.
 */

package com.riddlesvillage.core.collect;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.Validate;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.*;
import java.util.function.BiPredicate;

public class EnhancedMap<K, V> extends LinkedHashMap<K, V> implements JSONAware {

	private static final long serialVersionUID = -2780608617302194763L;

	public EnhancedMap() {}

	public EnhancedMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public EnhancedMap(int initialCapacity) {
		super(initialCapacity);
	}

	public EnhancedMap(Map<? extends K, ? extends V> m) {
		super(m);
	}

	public EnhancedMap(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}

	public EnhancedMap(String jsonString) {
		this((JSONObject) JSONValue.parse(jsonString));
	}

	public final boolean putIf(BiPredicate<K, V> check, K key, V value) {
		return putIf(check.test(key, value), key, value);
	}

	public final boolean putIf(boolean check, K key, V value) {
		if (!check) return false;
		put(key, value);
		return true;
	}

	public final boolean removeIf(BiPredicate<K, V> check, K key, V value) {
		return removeIf(check.test(key, value), key);
	}

	public final boolean removeIf(boolean check, K key) {
		if (!check) return false;
		remove(key);
		return true;
	}

	public final Map.Entry<K, V> getRandomEntry() {
		return new EnhancedList<>(entrySet()).getRandomElement();
	}

	@SafeVarargs
	public final Map.Entry<K, V> getRandomEntryExcluding(Map.Entry<K, V>... entries) {
		// Create a copy of this map with the specified entries removed
		return new EnhancedList<>(entrySet()).getRandomElementExcluding(entries);
	}

	public final ImmutableMap<K, V> getImmutableEntries() {
		return ImmutableMap.copyOf(this);
	}

	public EnhancedMap<K, V> revert() {
		int x = size();

		// Create a new HashMap with entries backward to this instance
		HashMap<K, V> tempMap = new HashMap<>(x);
		for (; x > 0; --x) {
			Map.Entry<K, V> entry = getEntry(x).get();
			tempMap.put(entry.getKey(), entry.getValue());
		}

		// Clear the current instance of the map
		clear();
		// Put the reverted entries from the HashMap back into the current instance
		putAll(tempMap);
		return this;
	}

	public Optional<Map.Entry<K, V>> getEntry(int position) {
		position = Math.abs(position);

		Set<Map.Entry<K, V>> entries = entrySet();

		int j = 0;
		for (Map.Entry<K, V> entry : entries)
			if (j++ == position) return Optional.of(entry);

		return Optional.empty();
	}

	public int getIndex(K key) {
		int x = 0;
		for (Map.Entry<K, V> entry : entrySet()) {
			// For maps such that (K == V) we check for equality in entry.getValue() too
			if (entry.getKey().equals(key) || entry.getValue().equals(key)) return x;
			++x;
		}

		throw null;
	}

	@Override
	public final String toJSONString() {
		return JSONObject.toJSONString(this);
	}

	/**
	 * Parses a {@code JSON} {@code String} to a {@code Map<String, Object>}.
	 *
	 * @param 	json The JSON String to parse.
	 * @return 	The parsed json object in the form of a map.
	 */
	public static Map<String, Object> fromJson(String json) {
		Validate.notNull(json);
		return new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
		}.getType());
	}

	@Override
	public String toString() {
		return toJSONString();
	}

	public static <M extends Map<K, V>,
			K extends Comparable<? super K>,
			V extends Comparable<? super V>> M sort(M map, SortType type) {
		Validate.notNull(map);
		Validate.notNull(type);

		LinkedList<Map.Entry<K, V>> entries = new LinkedList<>(map.entrySet());
		Collections.sort(entries, (o1, o2) -> type.isKey() ?
						o1.getKey().compareTo(o1.getKey()) :
						o1.getValue().compareTo(o1.getValue())
		);

		EnhancedMap<K, V> tempMap = new EnhancedMap<>(map.size());
		for (Map.Entry<K, V> entry : entries) {
			tempMap.put(entry.getKey(), entry.getValue());
		}

		map.clear();
		map.putAll(type.isAscending() ? tempMap.revert() : tempMap);
		return map;
	}

	public enum SortType {

		/**
		 * Represents the sorting type where the Key is ascending.
		 */
		ASC_KEY(true,  true),

		/**
		 * Represents the sorting type where the Value is ascending.
		 */
		ASC_VAL(true,  false),

		/**
		 * Represents the sorting type where the Key is descending.
		 */
		DES_KEY(false, true),

		/**
		 * Represents the sorting type where the Value is descending.
		 */
		DES_VAL(false, false);

		private final boolean ascending;
		private final boolean key;

		SortType(boolean ascending, boolean key) {
			this.ascending = ascending;
			this.key = key;
		}

		public boolean isAscending() {
			return ascending;
		}

		public boolean isKey() {
			return key;
		}
	}
}