/*
 * rv_core
 * 
 * Created on 04 June 2017 at 2:42 PM.
 */

package com.riddlesvillage.core.player.event;

import com.riddlesvillage.core.player.CorePlayer;
import org.bukkit.entity.Player;

public class CorePlayerJumpEvent extends CoreProfileEvent {

	public CorePlayerJumpEvent(Player player) {
		super(CorePlayer.createIfAbsent(player));
	}

	@Override
	public final CorePlayer getProfile() {
		return (CorePlayer) super.getProfile();
	}
}