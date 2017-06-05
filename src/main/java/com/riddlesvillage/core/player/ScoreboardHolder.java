/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player;

import com.riddlesvillage.core.scoreboard.ScoreboardFactory;
import org.bukkit.entity.Player;

/**
 * Represents a live client who is able to hold a {@link
 * org.bukkit.scoreboard.Scoreboard}.  The player must be
 * online and the class instance must be flexible enough
 * to adjust to very frequent calls to {@link #getScoreboardLayout()},
 * as the scoreboard could be updated a few times a second
 * per player.
 *
 * @see ScoreboardFactory
 */
public interface ScoreboardHolder {


	/**
	 * @return	The player's unique, personal premade layout
	 * 			of the scoreboard, used for {@code ScoreboardUpdater}
	 */
	ScoreboardFactory getScoreboardLayout();


	/**
	 * @return The Bukkit player instance that holds this player.
	 */
	Player getBukkitPlayer();
}