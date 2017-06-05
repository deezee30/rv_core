/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.profile;

import com.mongodb.async.client.MongoCollection;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.CorePlayerManager;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

/**
 * An interface symbolizing any user that can interact with the
 * database.
 *
 * The profile must have two default values, the name of the player
 * and the unique identifier ({@link UUID}) for that player.  The
 * values are not required to be correct, depending on the subclass.
 *
 * Additionally the profile interface provides the link between the
 * online {@link CorePlayer} instance provided both profiles match.
 *
 * @see CoreProfileList
 * @see CorePlayer
 */
public interface CoreProfile {


	/**
	 * Static instance of the online player manager singleton class.
	 */
	CorePlayerManager PLAYER_MANAGER = CorePlayerManager.getInstance();


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
	 * @return The last known IP address the player has used or current one
	 */
	String getIp();


	/**
	 * @return A {@link List} of previously connected with IP addresses
	 */
	List<String> getIpHistory();


	/**
	 * @return A {@link List} of previously connected with usernames
	 */
	List<String> getNameHistory();


	/**
	 * Checks if this profile has played on this server before.
	 *
	 * @return 	{@code true} if the profile has played before,
	 * 			otherwise {@code false}
	 */
	boolean hasPlayed();


	/**
	 * Checks if this profile is currently online,
	 *
	 * @return	{@code true} if they are online,
	 * 			{@code false} if otherwise
	 */
	default boolean isOnline() {
		return toCorePlayer() != null;
	}


	/**
	 * Checks if the player {@link #isOnline()} and returns the
	 * only {@link CorePlayer} instance that has been found
	 * according to both users' {@link #getUuid()}.
	 *
	 * @return	A {@code CorePlayer} instance if one has been found.
	 * 			If {@link #isOnline()} is {@code false}, {@code null}
	 * 			is returned.
	 * @see     CorePlayer
	 * @see     CorePlayerManager
	 */
	default CorePlayer toCorePlayer() {
		return PLAYER_MANAGER.get(getUuid());
	}

	MongoCollection<Document> getCollection();

	void loadStats(Document stats);
}