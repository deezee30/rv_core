/*
 * rv_core
 * 
 * Created on 01 June 2017 at 11:28 PM.
 */

package com.riddlesvillage.core.api;

import com.riddlesvillage.core.player.GamePlayer;
import com.riddlesvillage.core.player.GamePlayerList;
import com.riddlesvillage.core.player.GamePlayerManager;

import java.util.UUID;

/**
 * An interface symbolizing any user that can interact with the
 * {@code MySQL} database.
 * <p>
 * The profile must have two default values, the name of the player
 * and the unique identifier ({@link UUID}) for that player.  The
 * values are not required to be correct, depending on the subclass.
 * <p>
 * Additionally the profile interface provides the link between the
 * online {@link GamePlayer} instance provided both profiles match.
 *
 * @see GamePlayerList
 * @see GamePlayer
 */
public interface CoreProfile {

	/**
	 * Static instance of the online player manager singleton class.
	 */
	GamePlayerManager PLAYER_MANAGER = GamePlayerManager.get();


	/**
	 * Returns the name of this profile.
	 *
	 * @return Profile name
	 */
	String getName();


	/**
	 * Gets the "friendly" name to display of this profile.
	 * This may include color.
	 *
	 * @return The friendly name
	 */
	String getDisplayName();


	/**
	 * Returns the universal unique identifier ({@link UUID}) of
	 * the profile.
	 *
	 * @return The profile's identifier
	 */
	UUID getUuid();


	/**
	 * Checks if this profile has played on this server before.
	 *
	 * @return {@code true} if the profile has played before,
	 * otherwise {@code false}
	 */
	boolean hasPlayed();


	/**
	 * Checks if this profile is currently online,
	 *
	 * @return    {@code true} if they are online,
	 * {@code false} if otherwise
	 */
	default boolean isOnline() {
		return toGamePlayer() != null;
	}


	/**
	 * Checks if the player {@link #isOnline()} and returns the
	 * only {@link GamePlayer} instance that has been found
	 * according to both users' {@link #getUuid()}.
	 *
	 * @return A {@code GamePlayer} instance if one has been found.
	 * If {@link #isOnline()} is {@code false}, {@code null}
	 * is returned.
	 * @see        GamePlayer
	 * @see        GamePlayerManager
	 */
	default GamePlayer toGamePlayer() {
		return PLAYER_MANAGER.get(getUuid());
	}
}