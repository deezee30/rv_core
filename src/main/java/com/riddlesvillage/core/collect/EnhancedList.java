/*
 * MaulssLib
 *
 * Created on 25 December 2014 at 3:20 PM.
 */

package com.riddlesvillage.core.collect;

import com.google.common.collect.ImmutableList;
import com.sun.istack.internal.NotNull;
import org.apache.commons.lang3.Validate;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONValue;

import java.util.*;
import java.util.function.Predicate;

public class EnhancedList<E> extends ArrayList<E> implements JSONAware {

	private static final long serialVersionUID = 3550244475910399738L;

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

	public EnhancedList(String jsonString) {
		this((JSONArray) JSONValue.parse(jsonString));
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

	@NotNull
	public final ImmutableList<E> getImmutableElements() {
		return ImmutableList.copyOf(this);
	}

	public String toReadableList(@NotNull String separator) {
		// Do not append "and" instead of the last separator by default
		return toReadableList(separator, false);
	}

	public String toReadableList(@NotNull String separator,
								 @NotNull boolean concaterator) {
		Validate.notNull(separator);

		StringBuilder builder = new StringBuilder();

		int len = size();
		for (int x = 0; x < len; ++x) {
			builder.append(get(x));

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

	@Override
	public String toJSONString() {
		return JSONArray.toJSONString(this);
	}

	@Override
	public String toString() {
		return toJSONString();
	}

	public boolean equals(List<?> other) {
		if (other == null) return false;

		for (int x = 0; x < size(); ++x) {
			if (!get(x).equals(other.get(x))) return false;
		}

		return true;
	}
}