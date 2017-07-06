/*
 * rv_core
 * 
 * Created on 05 July 2017 at 6:46 PM.
 */

package com.riddlesvillage.core.pgm.team;

import com.riddlesvillage.core.pgm.player.GamePlayer;
import com.riddlesvillage.core.pgm.player.GamePlayerList;
import org.bukkit.ChatColor;

import java.util.Iterator;

public interface ITeam extends Iterable<GamePlayer> {

    String getName();

    ChatColor getColor();

    boolean addPlayer(GamePlayer player);

    boolean removePlayer(GamePlayer player);

    GamePlayerList getPlayers();

    boolean isFriendlyFire();

    default boolean isSpectator() {
        return false;
    }

    default int getRating() {
        int sum = 0;

        for (GamePlayer player : this) {
            sum += player.getRating();
        }

        return sum / getPlayers().size();
    }

    double getRelativeRating();

    @Override
    default Iterator<GamePlayer> iterator() {
        return getPlayers().iterator();
    }
}