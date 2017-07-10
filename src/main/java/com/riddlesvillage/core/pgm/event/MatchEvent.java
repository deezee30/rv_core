/*
 * rv_core
 * 
 * Created on 10 July 2017 at 3:56 AM.
 */

package com.riddlesvillage.core.pgm.event;

import com.riddlesvillage.core.pgm.match.Match;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class MatchEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Match match;

    public MatchEvent(final Match match) {
        this.match = Validate.notNull(match);
    }

    public Match getMatch() {
        return match;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}