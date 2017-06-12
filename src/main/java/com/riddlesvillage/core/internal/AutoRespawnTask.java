/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal;

import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.player.manager.CorePlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class AutoRespawnTask extends BukkitRunnable {

	private final Player player;

	public AutoRespawnTask(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		try {
			player.spigot().respawn();
		} catch (Throwable t) {
			Messaging.log(
					"An error occurred while attempting to force respawn player %s: %s",
					CorePlayerManager.getInstance().get(player.getUniqueId()), t
			);
		}
	}
}