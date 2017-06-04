/*
 * MySQLLib
 *
 * Created on 22 July 2014 at 2:45 AM.
 */

package com.riddlesvillage.core.database.value;

import com.riddlesvillage.core.util.MathUtil;
import org.apache.commons.lang3.Validate;

import java.io.Serializable;

public final class Value<O> implements Cloneable, Serializable {

	private static final long serialVersionUID = 35271921742643526L;

	private final O value;
	private final ValueType type;

	public Value(O value,
				 ValueType type) {
		this.value = Validate.notNull(value);
		this.type = Validate.notNull(type);
	}

	public boolean isInteger() {
		return MathUtil.isInteger(String.valueOf(value));
	}


	public ValueType getType() {
		return type;
	}


	public O getValue() {
		return value;
	}

	public int appendTo(int append) {
		if (!isInteger()) throw new IllegalStateException("Value " + value + " is not an integer");

		return type.equals(ValueType.SET) ? append
				: Integer.valueOf(value.toString())
				+ (type.equals(ValueType.GIVE) ? append : -append);
	}

	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public Value<O> clone() {
		return new Value<>(value, type);
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Value
				&& value.equals(((Value) other).value)
				&& type.equals(((Value) other).type);
	}
}