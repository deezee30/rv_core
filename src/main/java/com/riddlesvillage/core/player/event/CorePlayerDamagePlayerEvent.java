/*
 * rv_core
 * 
 * Created on 22 June 2017 at 9:54 PM.
 */

package com.riddlesvillage.core.player.event;

import com.riddlesvillage.core.player.CorePlayer;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class CorePlayerDamagePlayerEvent extends CorePlayerEvent {

    private final DamageCause cause;
    private final CorePlayer damaged;

    public CorePlayerDamagePlayerEvent(final EntityDamageByEntityEvent event) {
        super(MANAGER.get(Validate.notNull(event).getDamager().getUniqueId()));
        this.cause = event.getCause();
        this.damaged = MANAGER.get(event.getEntity().getUniqueId());
    }

    public CorePlayer getDamager() {
        return getPlayer();
    }

    public DamageCause getCause() {
        return cause;
    }

    public CorePlayer getDamaged() {
        return damaged;
    }
}