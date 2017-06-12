/*
 * rv_core
 * 
 * Created on 11 June 2017 at 9:29 PM.
 */

package com.riddlesvillage.core.chat;

import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.collect.EnhancedMap;
import com.riddlesvillage.core.internal.config.MainConfig;
import com.riddlesvillage.core.player.CorePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

class SpamFilter implements ChatBlockFilter {

	private final EnhancedMap<String, Integer> count = new EnhancedMap<>();

	@Override
	public boolean block(CorePlayer player, String message) {
		if (player.isHelper()) return false;

		boolean violation = false;

		// check for spam
		String name = player.getName();
		if (count.containsKey(name)) {
			int c = count.get(player.getName());
			if (c > MainConfig.getMaxMessages()) {
				count.remove(player.getName());
				violation = true;
			} else {
				putTemp(name, c + 1);
			}
		} else {
			putTemp(name, 1);
		}

		return violation;
	}

	@Override
	public Optional<String> getReason() {
		return Optional.of("chat.mute.no-spam");
	}

	@Override
	public boolean violate() {
		return true;
	}

	private void putTemp(String name, int messages) {
		count.put(name, messages);

		new BukkitRunnable() {

			@Override
			public void run() {
				if (count.containsKey(name)) {
					int c = count.get(name);
					if (c == 1) count.remove(name);
					else count.put(name, c - 1);
				}
			}
		}.runTaskLater(RiddlesCore.getInstance(), 20 * 20);
	}
}