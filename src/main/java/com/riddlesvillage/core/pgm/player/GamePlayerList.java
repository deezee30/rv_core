package com.riddlesvillage.core.pgm.player;

import com.riddlesvillage.core.collect.EnhancedList;

import java.util.Collection;

/**
 * Created by Matthew E on 7/5/2017.
 */
public class GamePlayerList extends EnhancedList<GamePlayer> {

    public GamePlayerList() {}

    public GamePlayerList(int initialCapacity) {
        super(initialCapacity);
    }

    public GamePlayerList(GamePlayer... elements) {
        super(elements);
    }

    public GamePlayerList(Collection<GamePlayer> c) {
        super(c);
    }
}