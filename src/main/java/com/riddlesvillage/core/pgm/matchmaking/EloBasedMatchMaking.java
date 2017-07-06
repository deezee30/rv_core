package com.riddlesvillage.core.pgm.matchmaking;

/**
 * Created by Matthew E on 7/6/2017.
 */
public class EloBasedMatchMaking implements MatchMaking<Integer, Integer> {
    private static final int ELO_RANGE = 150;

    @Override
    public boolean matches(Integer integer, Integer integer2) {
        return (Math.min(integer, integer2) <= ELO_RANGE) && (Math.max(integer, integer2) >= ELO_RANGE);
    }
}
