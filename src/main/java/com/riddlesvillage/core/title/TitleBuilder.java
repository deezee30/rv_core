package com.riddlesvillage.core.title;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class TitleBuilder implements ConfigurationSerializable {

	static {
		ConfigurationSerialization.registerClass(TitleBuilder.class);
	}

	private Optional<TitleMessage> title = Optional.empty();
	private Optional<TitleMessage> subTitle = Optional.empty();
	private boolean clear = false;

	public TitleBuilder() {}

	public TitleBuilder(TitleMessage title,
						TitleMessage subTitle) {
		withTitle(title).withTitle(subTitle);
	}

	public TitleBuilder withTitle(TitleMessage title) {
		if (title.getType().equals(TitleMessage.Type.TITLE))
			this.title = Optional.of(title);
		else
			this.subTitle = Optional.of(title);

		return this;
	}

	public TitleBuilder clear(boolean clear) {
		this.clear = clear;
		return this;
	}

	public Title build() {
		return new Title(title, subTitle, clear);
	}

	@Override
	public Map<String, Object> serialize() {
		return build().serialize();
	}

	public static TitleBuilder deserialize(Map<String, Object> data) {
		TitleBuilder builder = new TitleBuilder();

		if (data.containsKey("title"))
			builder.title = Optional.of((TitleMessage) data.get("title"));
		if (data.containsKey("subTitle"))
			builder.subTitle = Optional.of((TitleMessage) data.get("subTitle"));
		builder.clear = (boolean) data.get("clear");

		return builder;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TitleBuilder that = (TitleBuilder) o;
		return Objects.equals(clear, that.clear) &&
				Objects.equals(title, that.title) &&
				Objects.equals(subTitle, that.subTitle);
	}

	@Override
	public int hashCode() {
		return build().hashCode();
	}

	@Override
	public String toString() {
		return build().toString();
	}
}
