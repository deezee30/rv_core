package com.riddlesvillage.core.api.scoreboard;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Created by matt1 on 3/22/2017.
 */
public class PlayerScoreboard {

    private Player player;
    private Scoreboard vanillaScoreboard;
    private SideBar sideBar;
    private Team team;

    public PlayerScoreboard(Player player, Scoreboard vanillaScoreboard) {
        this.player = player;
        this.vanillaScoreboard = vanillaScoreboard;
    }

    public Player getPlayer() {
        return player;
    }

    public Scoreboard getVanillaScoreboard() {
        return vanillaScoreboard;
    }

    public PlayerScoreboard setVanillaScoreboard(Scoreboard vanillaScoreboard) {
        this.vanillaScoreboard = vanillaScoreboard;
        return this;
    }

    public void destroy() {
        vanillaScoreboard.getObjectives().forEach(Objective::unregister);
        vanillaScoreboard.getTeams().forEach(Team::unregister);
    }

    public SideBar getSideBar() {
        return sideBar;
    }

    public PlayerScoreboard setSideBar(SideBar sideBar) {
        this.sideBar = sideBar;
        return this;
    }

    public void setSuffix(String suffix) {
        Team team = getTeam();
        team.setSuffix(ChatColor.translateAlternateColorCodes('&', suffix));
    }

    public void setPrefix(String prefix) {
        Team team = getTeam();
        team.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
    }

    public Team getTeam() {
        return team;
    }

    public PlayerScoreboard setTeam(Team team) {
        this.team = team;
        this.team.addPlayer(player);
        return this;
    }

    private Objective objective;

    public void setUnderName(String underName, int score) {
        if (this.objective != null) {
            this.objective.unregister();
            this.objective = getVanillaScoreboard().registerNewObjective(player.getName() + ":u", "dummy");
            this.objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            this.objective.setDisplayName(underName);
            this.objective.getScore(player).setScore(score);
        } else {
            this.objective = getVanillaScoreboard().registerNewObjective(player.getName() + ":u", "dummy");
            this.objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            this.objective.setDisplayName(underName);
            this.objective.getScore(player).setScore(score);
        }
    }
}
