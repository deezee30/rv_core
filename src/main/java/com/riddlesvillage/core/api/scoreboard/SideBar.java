package com.riddlesvillage.core.api.scoreboard;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;

/**
 * Created by matt1 on 3/22/2017.
 */
public class SideBar {

    private String title;
    private Scoreboard scoreboard;
    private Player player;
    private HashMap<Integer, String> scoreboardMap;

    public SideBar() {
        this.scoreboardMap = new HashMap<>();
    }

    public SideBar setTitle(String title) {
        this.title = title;
        return this;
    }

    public SideBar setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        return this;
    }

    public SideBar setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public SideBar setScoreboardMap(HashMap<Integer, String> scoreboardMap) {
        this.scoreboardMap = scoreboardMap;
        return this;
    }


    public SideBar(String title, PlayerScoreboard playerScoreboard) {
        this.title = title;
        this.player = playerScoreboard.getPlayer();
        this.scoreboardMap = new HashMap<>();
        this.scoreboard = playerScoreboard.getVanillaScoreboard();
        Objective objective = this.scoreboard.registerNewObjective(player.getName() + ":s", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));
        playerScoreboard.setVanillaScoreboard(scoreboard);
    }

    public void show() {
        this.scoreboard.getObjective(player.getName() + ":s").unregister();
        Objective objective = this.scoreboard.registerNewObjective(player.getName() + ":s", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));
        scoreboardMap.forEach((slot, name) -> objective.getScore(ChatColor.translateAlternateColorCodes('&', name)).setScore(slot));
        player.setScoreboard(scoreboard);
    }

    public String getTitle() {
        return title;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Player getPlayer() {
        return player;
    }

    public HashMap<Integer, String> getScoreboardMap() {
        return scoreboardMap;
    }

    public SideBar setLine(int index, String text) {
       if (scoreboardMap.containsKey(index)) {
           scoreboardMap.remove(index);
       }
        scoreboardMap.put(index, ChatColor.translateAlternateColorCodes('&', text));
        return this;
    }

    public SideBar addLine(String text) {
        int index = -(scoreboardMap.values().size());
        setLine(index, text);
        return this;
    }

    public SideBar addBlankLine() {
        addLine("" + getString(scoreboardMap.size() ));
        return this;
    }

    private String getString(int size) {
        String s = "";
        for (int i = 0; i < size; i+= 2) {
            s += " ";
        }
        return s;
    }
}
