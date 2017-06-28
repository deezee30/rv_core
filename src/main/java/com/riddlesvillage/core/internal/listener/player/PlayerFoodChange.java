/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.listener.player;

import com.riddlesvillage.core.player.manager.CorePlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

final class PlayerFoodChange implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!CorePlayerManager.getInstance().get(event).canGetHungry()) {
            event.setCancelled(true);
        }
    }
}