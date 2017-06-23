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

	public CorePlayerDamageEntityEvent(EntityDamageByEntityEvent event) {
		super(MANAGER.get(Validate.notNull(event)));
		this.cause = event.getCause();
		this.damaged = event.getDamager();
	}

	public CorePlayer getDamager() {
		return getPlayer();
	}

	public Entity getDamaged() {
		return damaged;
	}

	public DamageCause getCause() {
		return cause;
	}
}