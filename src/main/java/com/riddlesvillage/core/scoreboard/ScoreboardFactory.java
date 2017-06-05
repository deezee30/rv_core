/*
 * RiddlesCore
 */

package com.riddlesvillage.core.scoreboard;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.collect.EnhancedMap;
import com.riddlesvillage.core.player.ScoreboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;

public class ScoreboardFactory implements Cloneable {

	private final Map<String, Object>	placeholders = new EnhancedMap<>();
	private final Scoreboard			scoreboard;

	private String                  title;
	private EnhancedList<Team> teams           = new EnhancedList<>();
	private Map<String, Integer> 	scores 			= new EnhancedMap<>();

	public ScoreboardFactory(String title) {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.title = title;
	}

	public void blankLine() {
		add(" ");
	}

	public void add(String text) {
		add(text, null);
	}

	public void add(String text, Integer score) {
		text = applyPlaceholders(ChatColor.translateAlternateColorCodes('&', text));
		while (scores.containsKey(text)) text += ChatColor.RESET;
		if (text.length() > 48) text = text.substring(0, 47);
		scores.put(text, score);
	}

	private Map.Entry<Team, String> createTeam(String text) {
		String result;
		if (text.length() <= 16) return new AbstractMap.SimpleEntry<>(null, text);
		Team team = scoreboard.registerNewTeam("text-" + scoreboard.getTeams().size());
		Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();
		team.setPrefix(iterator.next());
		result = iterator.next();
		if (text.length() > 32) team.setSuffix(iterator.next());
		teams.add(team);
		return new AbstractMap.SimpleEntry<>(team, result);
	}

	public void applyPlaceholders(ImmutableMap<String, Object> placeholders) {
		this.placeholders.putAll(placeholders);
	}

	public Scoreboard build() {
		title = applyPlaceholders(title);
		Objective obj = scoreboard.registerNewObjective((title.length() > 16 ? title.substring(0, 15) : title), "dummy");
		obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		int index = scores.size();

		for (Map.Entry<String, Integer> text : scores.entrySet()) {
			Map.Entry<Team, String> team = createTeam(text.getKey());
			Integer score = text.getValue() != null ? text.getValue() : index;
			String row = team.getValue();
			if (row.length() > 48) row = row.substring(0, 47);
			if (!Strings.isNullOrEmpty(row) && team.getKey() != null) {
				team.getKey().addEntry(ChatColor.translateAlternateColorCodes('&', row));
			}
			obj.getScore(ChatColor.translateAlternateColorCodes('&', row)).setScore(score);
			--index;
		}

		return scoreboard;
	}

	public void reset() {
		title = null;
		scores.clear();
		teams.forEach(Team::unregister);
		teams.clear();
		placeholders.clear();
	}

	public Scoreboard send(ScoreboardHolder... players) {
		for (ScoreboardHolder p : players)
			p.getBukkitPlayer().setScoreboard(scoreboard);

		return scoreboard;
	}

	@Override
	public ScoreboardFactory clone() {
		ScoreboardFactory newScoreboard = new ScoreboardFactory(title);
		newScoreboard.scores = scores;
		newScoreboard.teams = teams;
		return newScoreboard;
	}

	private String applyPlaceholders(final String text) {
		final String[] finalText = {text};
		placeholders.forEach((s, o) -> {
			if (finalText[0].contains(s)) {
				finalText[0] = finalText[0].replace(s, String.valueOf(o));
			}
		});
		return finalText[0];
	}
}