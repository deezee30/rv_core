/*
 * rv_core
 * 
 * Created on 02 July 2017 at 11:37 PM.
 */

package com.riddlesvillage.core.pgm.rating.elo;

public enum EloMatchOutcome {

    WIN (1.0),
    DRAW(0.5),
    LOSS(0.0);

    private final double score;

    EloMatchOutcome(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }
}