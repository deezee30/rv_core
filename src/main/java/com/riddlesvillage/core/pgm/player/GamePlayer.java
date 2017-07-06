package com.riddlesvillage.core.pgm.player;

import com.riddlesvillage.core.pgm.rating.Competitor;
import com.riddlesvillage.core.player.CorePlayer;

/**
 * Created by Matthew E on 7/5/2017.
 */
public abstract class GamePlayer implements Competitor {

    private CorePlayer corePlayer;

    public GamePlayer(CorePlayer corePlayer) {
        this.corePlayer = corePlayer;
    }

    public CorePlayer getCorePlayer() {
        return corePlayer;
    }
}
