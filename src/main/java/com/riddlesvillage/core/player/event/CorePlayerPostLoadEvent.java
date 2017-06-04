/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.event;

import com.riddlesvillage.core.player.CorePlayer;

/**
 * Event used to initialize other player objects that access
 * the RiddlesCore player API.
 *
 * <p>Constructing an instance of {@link CorePlayer} is quite
 * a heavy task, and hence {@code CorePlayer}s are not loaded
 * instantly after {@link org.bukkit.event.player.PlayerJoinEvent}
 * is called.  Any classes that rely on the data generated for
 * the {@code CorePlayer} object as quickly after instantiation
 * as possible should use this event for doing so.</p>
 */
public class CorePlayerPostLoadEvent extends CoreProfileEvent {

	private final boolean newComer;

	/**
	 * Calls the load event for {@code CorePlayer} implementations.
	 *
	 * @param	player
	 * 			The player associated.
	 * @param	newComer
	 * 			Whether or not he is a newcomer.
	 */
	public CorePlayerPostLoadEvent(CorePlayer player, boolean newComer) {
		super(player);
		this.newComer = newComer;
	}

	@Override
	public final CorePlayer getProfile() {
		return (CorePlayer) super.getProfile();
	}

	/**
	 * Returns whether or not this is the first time the player
	 * joined the server.
	 *
	 * <p>The player's newcomer status is dependent on if he
	 * exists in the global database table or not.</p>
	 *
	 * @return	{@code true} if it's the player's first time
	 * 			joining. {@code false} if not.
	 */
	public final boolean isNew() {
		return newComer;
	}
}