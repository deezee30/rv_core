/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player;

import com.riddlesvillage.core.Messaging;
import org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;

/**
 * The enum Rank.
 */
public enum Rank {

    DEFAULT (0,     "Member",   ChatColor.GRAY),
    HELPER  (4,     "Helper",   ChatColor.AQUA),
    MOD     (5,     "Mod",      ChatColor.GREEN),
    DEV     (10,    "Dev",      ChatColor.DARK_GREEN),
    LEAD_DEV(10,    "Lead Dev", ChatColor.GOLD),
    ADMIN   (99999, "Admin",    ChatColor.BLUE);

    private final int id;
    private final String name;
    private final ChatColor color;

    Rank(int id, String name, ChatColor color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    /**
     * Gets the rank id.
     *
     * <p>A higher rank {@code ID} correlates to a
     * higher rank</p>
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the rank's friendly name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the rank's associated chat color.
     *
     * @return the color
     */
    public ChatColor getColor() {
        return color;
    }

    /**
     * Gets the rank's friendly display name
     *
     * <p>Includes the rank's chat color and
     * friendly name</p>
     *
     * @return the display name
     */
    public String getDisplayName() {
        return color + name;
    }

    /**
     * Gets the rank's format, including colored prefix
     * and display name.
     *
     * <p>Format is {@code &8[%COLOR%%DISPLAY_NAME%&8]&7}</p>
     *
     * @return the format
     */
    public String getFormat() {
        return ChatColor.translateAlternateColorCodes('&',
                Messaging.buildMessage("&8[%s&8]&7", getDisplayName()));
    }

    @Override
    public String toString() {
        return name();
    }

    /**
     * @param name the name of the rank to get
     * @return The rank according to the name specified
     */
    public static Rank byName(String name) {
        Validate.notNull(name);

        for (Rank rank : values()) {
            if (rank.name.equalsIgnoreCase(name)) return rank;
        }

        return DEFAULT;
    }
}