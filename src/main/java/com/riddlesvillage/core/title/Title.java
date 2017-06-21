package com.riddlesvillage.core.title;

import com.riddlesvillage.core.collect.EnhancedMap;
import com.riddlesvillage.core.player.CorePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class Title implements ConfigurationSerializable {

	static {
		ConfigurationSerialization.registerClass(Title.class);
	}

	private static final transient TitleHandler handler = new TitleHandler();

	private final boolean clear;
	private Optional<TitleMessage> title = Optional.empty();
	private Optional<TitleMessage> subTitle = Optional.empty();

	Title(Optional<TitleMessage> title,
		  Optional<TitleMessage> subTitle,
		  boolean clear) {
		this.title = title;
		this.subTitle = subTitle;
		this.clear = clear;
	}

	public void send(Player... players) {
        for (Player player : players) {
            send(CorePlayer.createIfAbsent(player));
        }
    }

	public void send(CorePlayer... players) {
		if (clear) {
			handler.send(handler.buildClearPacket(), players);
			return;
		}

		if (title.isPresent()) {
			TitleMessage title = this.title.get();
			handler.handleTitleSendPacket(title, players);
		}

		if (subTitle.isPresent()) {
			TitleMessage subTitle = this.subTitle.get();
			handler.handleTitleSendPacket(subTitle, players);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		EnhancedMap<String, Object> map = new EnhancedMap<>();

		map.put("clear", clear);
		map.putIf(title.isPresent(), "title", title.get());
		map.putIf(subTitle.isPresent(), "subTitle", subTitle.get());

		return map;
	}

	public static Title deserialize(Map<String, Object> data) {
		Optional<TitleMessage> title = Optional.empty();
		Optional<TitleMessage> subTitle = Optional.empty();
		boolean clear = false;

		if (data.containsKey("title"))
			title = Optional.of((TitleMessage) data.get("title"));
		if (data.containsKey("subTitle"))
			subTitle = Optional.of((TitleMessage) data.get("subTitle"));
		clear = (boolean) data.get("clear");

		return new Title(title, subTitle, clear);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Title that = (Title) o;
		return Objects.equals(clear, that.clear) &&
				Objects.equals(title, that.title) &&
				Objects.equals(subTitle, that.subTitle);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, subTitle, clear);
	}

	@Override
	public String toString() {
		return "Title{" +
				"title=" + title +
				", subTitle=" + subTitle +
				", clear=" + clear +
				'}';
	}

	public static TitleBuilder builder() {
		return new TitleBuilder();
	}
}
