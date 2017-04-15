package com.riddlesvillage.core.api.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;

/**
 * Created by matt1 on 3/22/2017.
 */
public class ScoreBoardManager {

    private static ScoreBoardManager manager;
    private HashMap<Player, PlayerScoreboard> scoreboardMap;

    public static ScoreBoardManager getManager() {
        if (manager == null) {
            manager = new ScoreBoardManager();
        }
        return manager;
    }

    public ScoreBoardManager() {
        manager = this;
        this.scoreboardMap = new HashMap<>();
    }

    public void destroyAllBoards() {
        this.scoreboardMap.values().forEach(PlayerScoreboard::destroy);
    }

    public PlayerScoreboard getScoreboard(Player player) {
        if (scoreboardMap.containsKey(player)) {
            return scoreboardMap.get(player);
        }
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        PlayerScoreboard playerScoreboard = new PlayerScoreboard(player, scoreboard);
        this.scoreboardMap.put(player, playerScoreboard);
        return playerScoreboard;
    }

    public void removeScoreboard(Player player) {
        if (scoreboardMap.containsKey(player)) {
            PlayerScoreboard scoreboard = getScoreboard(player);
            scoreboard.destroy();
        }
    }
}
