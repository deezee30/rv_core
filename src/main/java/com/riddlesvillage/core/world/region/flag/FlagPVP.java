/*
 * rv_core
 * 
 * Created on 28 June 2017 at 3:13 PM.
 */

package com.riddlesvillage.core.world.region.flag;

import com.riddlesvillage.core.player.event.CorePlayerDamagePlayerEvent;
import org.bukkit.Location;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagPVP implements IFlag<CorePlayerDamagePlayerEvent> {

    @Override
    public Class<CorePlayerDamagePlayerEvent> getEvent() {
        return CorePlayerDamagePlayerEvent.class;
    }

    @Override
    public Location getLocationOfAction(CorePlayerDamagePlayerEvent event) {
        return event.getDamaged().getLocation();
    }

    @Override
    public Optional<Predicate<CorePlayerDamagePlayerEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(CorePlayerDamagePlayerEvent event) {
        return Optional.empty();
    }
}