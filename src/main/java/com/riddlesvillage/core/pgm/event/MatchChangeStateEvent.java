/*
 * rv_core
 * 
 * Created on 10 July 2017 at 3:56 AM.
 */

package com.riddlesvillage.core.pgm.event;

import com.riddlesvillage.core.pgm.match.Match;
import com.riddlesvillage.core.pgm.match.MatchState;
import org.apache.commons.lang3.Validate;

public class MatchChangeStateEvent extends MatchEvent {

    private final MatchState
            oldState,
            newState;

    public MatchChangeStateEvent(final Match match,
                                 final MatchState oldState,
                                 final MatchState newState) {
        super(match);
        this.oldState = Validate.notNull(oldState);
        this.newState = Validate.notNull(newState);
    }

    public MatchState getOldState() {
        return oldState;
    }

    public MatchState getNewState() {
        return newState;
    }
}