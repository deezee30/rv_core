/*
 * rv_core
 * 
 * Created on 04 June 2017 at 2:28 PM.
 */

package com.riddlesvillage.core.internal.listener.player;

import com.riddlesvillage.core.player.event.CorePlayerJumpEvent;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

final class PlayerJump implements Listener {

	private Map<UUID, Integer> jumps = new HashMap<>();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		jumps.put(e.getPlayer().getUniqueId(), e.getPlayer().getStatistic(Statistic.JUMP));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent e) {
		jumps.remove(e.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (e.getFrom().getY() < e.getTo().getY()) {
			int current = player.getStatistic(Statistic.JUMP);
			int last = jumps.getOrDefault(player.getUniqueId(), -1);

			if (last != current) {
				jumps.put(player.getUniqueId(), current);

				double yDif = (long) ((e.getTo().getY() - e.getFrom().getY()) * 1000) / 1000d;

				if ((yDif < 0.035 || yDif > 0.037) && (yDif < 0.116 || yDif > 0.118)) {
					Bukkit.getPluginManager().callEvent(new CorePlayerJumpEvent(player));
				}
			}
		}
	}
}