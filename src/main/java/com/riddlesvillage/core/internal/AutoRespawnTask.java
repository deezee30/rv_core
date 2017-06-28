/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.player.manager.CorePlayerManager;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class AutoRespawnTask extends BukkitRunnable {

    private final Player player;

    public AutoRespawnTask(final Player player) {
        this.player = Validate.notNull(player);
    }

    @Override
    public void run() {
        try {
            if (player.isOnline()) player.spigot().respawn();
        } catch (Throwable t) {
            Core.log(
                    "An error occurred while attempting to force respawn player %s: %s",
                    CorePlayerManager.getInstance().get(player.getUniqueId()), t
            );
        }
    }
}