package com.riddlesvillage.core.pgm.option;

import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.pgm.SpawnReason;
import com.riddlesvillage.core.pgm.player.GamePlayer;
import org.bukkit.Location;

import java.util.Optional;

public class GameOptions {

    private SpawnStrategy spawnStrategy = new DefaultRandomSpawnStrategy();
    private boolean useRatings = false;
    private Integer maxPlayers = null;

    public GameOptions setSpawnStrategy(final SpawnStrategy spawnStrategy) {
        this.spawnStrategy = spawnStrategy;
        return this;
    }

    public SpawnStrategy getSpawnStrategy() {
        return spawnStrategy;
    }

    public GameOptions setUseRatings(boolean useRatings) {
        this.useRatings = useRatings;
        return this;
    }

    public boolean useRatings() {
        return useRatings;
    }

    public GameOptions setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }

    public Optional<Integer> getMaxPlayers() {
        return Optional.ofNullable(maxPlayers);
    }

    public static class DefaultRandomSpawnStrategy implements SpawnStrategy {

        private DefaultRandomSpawnStrategy() {}

        @Override
        public Location findSpawn(final GamePlayer player,
                                  final EnhancedList<Location> possibleSpawns,
                                  final SpawnReason reason) {
            return possibleSpawns.getRandomElement();
        }
    }
}
