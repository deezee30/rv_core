/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.listener.player;

import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.internal.config.MainConfig;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.Rank;
import com.riddlesvillage.core.player.profile.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

final class PlayerChat implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event) {
		CorePlayer player = CoreProfile.PLAYER_MANAGER.get(event.getPlayer().getName());

		// Allow only premiums and staff to chat during premium-chat mode
		if (RiddlesCore.getSettings().isPremiumChat() && !(player.isPremium() || player.isMod())) {
			player.sendMessage("chat.premium-only");
			event.setCancelled(true);
			return;
		}

		// Make sure custom chat format is enabled
		if (!MainConfig.doShowRankInChat()) {
			return;
		}

		// Escape %* delimiters for String.format(...)
		String msg = event.getMessage().replace("%", "%%");

		// Play a sound for players mentioned in the message
		for (CorePlayer p : CoreProfile.PLAYER_MANAGER) {
			if (msg.toLowerCase().contains(p.getName().toLowerCase())) {
				p.getPlayer().playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, .25F, 2F);
			}
		}

		Rank rank = player.getRank();

		event.setFormat(String.format(
				ChatColor.translateAlternateColorCodes('&', RiddlesCore.getSettings().get(player.getLocale(), "chat.format")),
				rank.getDisplayName(),
				player.getDisplayName(),
				msg
		));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		CorePlayer player = CorePlayer.PLAYER_MANAGER.get(event);
		String command = event.getMessage().substring(1).split(" ")[0];
		if (player.isCommandsBlocked()
				&& !RiddlesCore.getSettings().getAllowedCommands().contains(command.toLowerCase())) {
			event.setCancelled(true);
			player.sendMessage("command.blocked", new String[] {"$command"}, command);
		}
	}
}