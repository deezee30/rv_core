/*
 * rv_core
 * 
 * Created on 13 June 2017 at 2:13 PM.
 */

package com.riddlesvillage.core.scoreboard;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.collect.EnhancedMap;
import com.riddlesvillage.core.scoreboard.handler.CoreScoreboardHandler;
import com.riddlesvillage.core.scoreboard.handler.IScoreboardHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

public final class Scoreboards {

	public static final int MAX_WIDTH = 24;

	// Disable inialization
	private Scoreboards() {}

	public static Map<String, Integer> orderedRows(List<String> rows) {
		int len = rows.size();
		EnhancedMap<String, Integer> map = new EnhancedMap<>(len);
		for (int i = 0; i < len; i++) {
			map.put(rows.get(i), len - i - 1);
		}

		return map;
	}

	public static IScoreboardHandler newScoreboard(IScoreboard scoreboard, long refreshRateTicks) {
		CoreScoreboardHandler board = new CoreScoreboardHandler(scoreboard).refresh();

		new BukkitRunnable() {

			@Override
			public void run() {
				if (scoreboard.getHolder().getBukkitPlayer().isOnline()) {
					board.refresh();
				} else {
					board.destroy();
					cancel();
				}
			}
		}.runTaskTimer(Core.get(), refreshRateTicks, refreshRateTicks);

		return board;
	}
}