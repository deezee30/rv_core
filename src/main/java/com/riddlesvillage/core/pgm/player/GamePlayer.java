package com.riddlesvillage.core.pgm.player;

import com.riddlesvillage.core.pgm.rating.Competitor;
import com.riddlesvillage.core.pgm.team.Team;
import com.riddlesvillage.core.player.CorePlayer;
import org.bukkit.Location;

import java.util.Optional;

public abstract class GamePlayer implements Competitor {

    private CorePlayer corePlayer;
    private Optional<Team> team;

    public GamePlayer(CorePlayer corePlayer) {
        this.corePlayer = corePlayer;
    }

    public CorePlayer toCorePlayer() {
        return corePlayer;
    }

    public Location getLocation() {
        return corePlayer.getLocation();
    }
}
