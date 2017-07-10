/*
 * rv_core
 * 
 * Created on 10 July 2017 at 3:07 AM.
 */

package com.riddlesvillage.core.pgm.team;

import org.bukkit.ChatColor;

public interface SpectatorTeam extends Team {

    @Override
    default String getName() {
        return "Spectator Team";
    }

    @Override
    default ChatColor getColor() {
        return ChatColor.GRAY;
    }

    @Override
    default boolean isFriendlyFire() {
        return false;
    }

    @Override
    default boolean isSpectator() {
        return true;
    }
}