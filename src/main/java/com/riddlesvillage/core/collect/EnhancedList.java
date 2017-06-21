/*
 * MaulssLib
 *
 * Created on 25 December 2014 at 3:20 PM.
 */

package com.riddlesvillage.core.collect;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.Validate;
import org.json.simple.JSONAware;

import java.util.*;
import java.util.function.Predicate;

public class EnhancedList<E> extends ArrayList<E> implements JSONAware {

	private static final long serialVersionUID = 3550244475910399738L;
	private final static Gson gson = new GsonBuilder()
			.disableHtmlEscaping()
			.serializeNulls()
			.create();

	public EnhancedList() {}

	public EnhancedList(int initialCapacity) {
		super(initialCapacity);
	}

	@SafeVarargs
	public EnhancedList(E... elements) {
		this(Arrays.asList(elements));
	}

	public EnhancedList(Collection<? extends E> c) {
		super(c);
	}

	public final boolean addIf(Predicate<E> check, E element) {
		return addIf(check.test(element), element);
	}

	public final boolean addIf(boolean check, E element) {
		return check && add(element);
	}

	public final boolean removeIf(Predicate<E> check, E element) {
		return removeIf(check.test(element), element);
	}

	public final boolean removeIf(boolean check, E element) {
		return check && remove(element);
	}

	public final E getRandomElement() {
		return size() == 0 ? null : get(size() == 1 ? 0 : new Random().nextInt(size()));
	}

	@SafeVarargs
	public final E getRandomElementExcluding(E... elements) {
		// Create a copy of this list with the specified elements removed
		EnhancedList<E> list = new EnhancedList<>(this);
		new EnhancedList<>(elements).forEach(list::remove);
		return list.getRandomElement();
	}

	public final ImmutableList<E> getImmutableElements() {
		return ImmutableList.copyOf(this);
	}

	public String toReadableList(String separator) {
		// Do not append "and" instead of the last separator by default
		return toReadableList(separator, false);
	}

	public String toReadableList(String separator,
								 boolean concaterator) {
		Validate.notNull(separator);

		StringBuilder builder = new StringBuilder();

		int len = size();
		for (int x = 0; x < len; ++x) {
			builder.append(get(x).toString());

			// if concaterator == true, append the word "and" instead
			// of the last separator to make the list more readable.
			// For example, having a list such as [String1,String2,String3]
			// will look like this with separator ", " and concaterator
			// disabled: "String1, String2, String3" and enabled:
			// "String1, String2 and String3".
			if (concaterator) {
				if (x <= len - 3) builder.append(separator);
				if (x == len - 2) builder.append(" and ");
			} else {
				if (x != len - 1) builder.append(separator);
			}
		}

		return builder.toString();
	}

	public Gson getGson() {
		return gson;
	}

	public String toJson() {
		return getGson().toJson(this);
	}

	@Override
	public final String toJSONString() {
		return toJson();
	}

	@Override
	public String toString() {
		return toJson();
	}

	public boolean equals(List<?> other) {
		if (other == null) return false;

		for (int x = 0; x < size(); ++x) {
			if (!get(x).equals(other.get(x))) return false;
		}

		return true;
	}

	public static <T> EnhancedList<T> fromJson(String json) {
		return fromJson(gson, json);
	}

	public static <T> EnhancedList<T> fromJson(Gson gson, String json) {
		return gson.fromJson(json, new TypeToken<EnhancedList<T>>() {}.getType());
	}
}