/*
 * rv_core
 * 
 * Created on 13 June 2017 at 2:06 PM.
 */

package com.riddlesvillage.core.scoreboard;

import com.riddlesvillage.core.collect.EnhancedMap;
import com.riddlesvillage.core.player.ScoreboardHolder;
import com.riddlesvillage.core.util.Scroller;

import java.util.Map;

public class ScrollerScoreboard implements IScoreboard {

	private final ScoreboardHolder holder;
	private final int maxWidth;
	private final int spaceBetween;

	private Scroller title;
	private EnhancedMap<Scroller, Integer> rows;

	public ScrollerScoreboard(ScoreboardHolder holder, int maxWidth, int spaceBetween) {
		this.holder = holder;
		this.maxWidth = maxWidth;
		this.spaceBetween = spaceBetween;
		update();
	}

	@Override
	public String getTitle() {
		String title = this.title.next();
		boolean a = true;
		while (title.length() < maxWidth - 5) {
			if (a = !a) title = " " + title;
			else title += " ";
		}

		return title;
	}

	@Override
	public Map<String, Integer> getRows() {
		EnhancedMap<String, Integer> rows = new EnhancedMap<>(this.rows.size());

		for (Map.Entry<Scroller, Integer> entry : this.rows.entrySet()) {
			rows.put(entry.getKey().next(), entry.getValue());
		}

		return rows;
	}

	@Override
	public ScoreboardHolder getHolder() {
		return holder;
	}

	@Override
	public void update() {
		ScoreboardContainer scoreboard = holder.getScoreboard();
		if (scoreboard == null) return;

		String newTitle = scoreboard.getTitle();
		Map<String, Integer> newRows = scoreboard.getRows();

		title = new Scroller(
				newTitle == null
						? holder.getBukkitPlayer().getDisplayName()
						: newTitle,
				maxWidth,
				spaceBetween
		);

		if (rows == null)
			rows = new EnhancedMap<>(newRows.size());

		rows.clear();
		for (Map.Entry<String, Integer> entry : newRows.entrySet()) {
			rows.put(new Scroller(entry.getKey(), maxWidth, spaceBetween), entry.getValue());
		}
	}

	@Override
	public int getMaxWidth() {
		return maxWidth;
	}
}