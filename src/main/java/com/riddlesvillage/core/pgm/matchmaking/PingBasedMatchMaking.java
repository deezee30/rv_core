package com.riddlesvillage.core.pgm.matchmaking;

/**
 * Created by Matthew E on 7/6/2017.
 */
public class PingBasedMatchMaking implements MatchMaking<Integer, Integer> {
    private static final int MS_RANGE = 30;

    @Override
    public boolean matches(Integer integer, Integer integer2) {
        return (Math.min(integer, integer2) <= MS_RANGE) && (Math.max(integer, integer2) >= MS_RANGE);
    }
}