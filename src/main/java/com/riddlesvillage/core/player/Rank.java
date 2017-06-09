/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player;

import com.riddlesvillage.core.Messaging;
import org.bukkit.ChatColor;

public enum Rank {

	DEFAULT	(0,		"Member",	ChatColor.GRAY),
	HELPER	(1,		"Helper",	ChatColor.AQUA),
	MOD		(5,		"Mod",		ChatColor.GREEN),
	DEV		(10,	"Dev",		ChatColor.DARK_GREEN),
	LEAD_DEV(10,	"Lead Dev",	ChatColor.GOLD),
	ADMIN	(99999, "Admin",	ChatColor.BLUE);

	private final int id;
	private final String name;
	private final ChatColor color;

	Rank(int id, String name, ChatColor color) {
		this.id = id;
		this.name = name;
		this.color = color;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ChatColor getColor() {
		return color;
	}

	public String getDisplayName() {
		return color + name;
	}

	public String getFormat() {
		return ChatColor.translateAlternateColorCodes('&',
				Messaging.buildMessage("&8[%s&8]&7", getDisplayName()));
	}

	@Override
	public String toString() {
		return name();
	}

	public static Rank byName(String name) {
		for (Rank rank : values()) {
			if (rank.getName().equalsIgnoreCase(name)) return rank;
		}

		return DEFAULT;
	}
}