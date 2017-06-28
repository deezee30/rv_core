/*
 * rv_core
 * 
 * Created on 12 June 2017 at 5:34 PM.
 */

package com.riddlesvillage.core.player;

import com.riddlesvillage.core.Violation;

public abstract class CorePlayerViolation extends Violation<CorePlayer> {

    protected CorePlayerViolation(final CorePlayer player,
                                  final int toleration) {
        super(player, toleration);
    }

    public final CorePlayer getPlayer() {
        return getTarget();
    }
}