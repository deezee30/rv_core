/*
 * rv_core
 * 
 * Created on 22 June 2017 at 9:54 PM.
 */

package com.riddlesvillage.core.player.event;

import com.riddlesvillage.core.player.CorePlayer;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class CorePlayerDamageEntityEvent extends CorePlayerEvent {

    private final DamageCause cause;
    private final Entity damaged;

    public CorePlayerDamageEntityEvent(final EntityDamageByEntityEvent event) {
        super(MANAGER.get(Validate.notNull(event).getDamager().getUniqueId()));
        this.cause = event.getCause();
        this.damaged = event.getEntity();
    }

    public CorePlayer getDamager() {
        return getPlayer();
    }

    public DamageCause getCause() {
        return cause;
    }

    public Entity getDamaged() {
        return damaged;
    }
}