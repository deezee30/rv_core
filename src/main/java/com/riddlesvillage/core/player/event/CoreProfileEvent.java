/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.event;

import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.profile.CoreProfile;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Handles all events in relation to the API's default
 * user object, {@link CoreProfile}.
 *
 * <p>Events triggered here can be for both, online and
 * offline users.  However, the user must at least exist
 * in the global database table where {@link
 * CorePlayer}s data is stored.</p>
 */
public abstract class CoreProfileEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final CoreProfile profile;

	protected CoreProfileEvent(CoreProfile profile) {
		this.profile = Validate.notNull(profile);
	}

	public CoreProfile getProfile() {
		return profile;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}