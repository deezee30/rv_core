/*
 * rv_core
 * 
 * Created on 21 June 2017 at 10:20 PM.
 */

package com.riddlesvillage.core.title;

import com.riddlesvillage.core.collect.EnhancedMap;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.Map;
import java.util.Optional;

public final class TitleMessage implements ConfigurationSerializable {

	static {
		ConfigurationSerialization.registerClass(TitleMessage.class);
	}

	private final Type type;
	private Optional<String> message = Optional.empty();
	private Optional<Integer> after = Optional.empty();
	private Optional<Integer> stay = Optional.empty();
	private Optional<Integer> fadeIn = Optional.empty();
	private Optional<Integer> fadeOut = Optional.empty();

	public TitleMessage(Type type) {
		this.type = Validate.notNull(type);
	}

	public TitleMessage(Type type, String message) {
		this.type = Validate.notNull(type);
		withMessage(message);
	}

	public TitleMessage withMessage(String message) {
		this.message = Optional.ofNullable(message);
		return this;
	}

	public TitleMessage after(int ticks) {
		after = Optional.of(ticks);
		return this;
	}

	public TitleMessage stayFor(int ticks) {
		stay = Optional.of(ticks);
		return this;
	}

	public TitleMessage fadeInFor(int ticks) {
		fadeIn = Optional.of(ticks);
		return this;
	}

	public TitleMessage fadeOutFor(int ticks) {
		fadeOut = Optional.of(ticks);
		return this;
	}

	public Type getType() {
		return type;
	}

	public Optional<String> getMessage() {
		return message;
	}

	public Optional<Integer> getAfter() {
		return after;
	}

	public Optional<Integer> getStay() {
		return stay;
	}

	public Optional<Integer> getFadeIn() {
		return fadeIn;
	}

	public Optional<Integer> getFadeOut() {
		return fadeOut;
	}

	public boolean isAnimated() {
		return stay.isPresent() || fadeIn.isPresent() || fadeOut.isPresent();
	}

	public boolean isDelayed() {
		return after.isPresent();
	}

	@Override
	public Map<String, Object> serialize() {
		EnhancedMap<String, Object> map = new EnhancedMap<>();

		map.put("type", type);
		map.putIf(message.isPresent(), "message", message);
		map.putIf(after.isPresent(), "after", after.get());
		map.putIf(stay.isPresent(), "stay", stay.get());
		map.putIf(fadeIn.isPresent(), "fadeIn", fadeIn.get());
		map.putIf(fadeOut.isPresent(), "fadeOut", fadeOut.get());

		return map;
	}

	public static TitleMessage deserialize(Map<String, Object> data) {
		return new TitleMessage(Type.valueOf(data.get("type").toString()))
				.withMessage(data.get("message").toString())
				.after((int) data.get("after"))
				.stayFor((int) data.get("stay"))
				.fadeInFor((int) data.get("fadeIn"))
				.fadeOutFor((int) data.get("fadeOut"));
	}

	public enum Type {
		TITLE,
		SUBTITLE;

		@Override
		public String toString() {
			return name();
		}
	}
}