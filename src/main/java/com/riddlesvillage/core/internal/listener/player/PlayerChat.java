/*
 * RiddlesCore
 */

package com.riddlesvillage.core.internal.listener.player;

import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.chat.ChatBlockFilter;
import com.riddlesvillage.core.chat.ChatFilters;
import com.riddlesvillage.core.internal.config.MainConfig;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.profile.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Optional;
import java.util.regex.Pattern;

final class PlayerChat implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event) {
		CorePlayer player = CoreProfile.PLAYER_MANAGER.get(event);

		// Allow only premiums and staff to chat during premium-chat mode
		if (RiddlesCore.getSettings().isPremiumChat() && !(player.isPremium() || player.isMod())) {
			player.sendMessage("chat.premium-only");
			event.setCancelled(true);
			return;
		}

		// Escape %* delimiters for String.format(...)
		String msg = event.getMessage().replace("%", "%%");

		// Block all messages that aren't supposed to be sent
		for (ChatBlockFilter filter : ChatFilters.getInstance()) {
			if (event.isCancelled()) break;

			if (filter.block(player, msg)) {
				event.setCancelled(true);

				// why?
				Optional<String> reason = filter.getReason();
				if (reason.isPresent()) {
					player.sendMessage(reason.get());
				}

				// add chat violation
				if (filter.violate())
					player.getViolationManager().getChatViolation().addViolation();
			}
		}

		// Make sure custom chat format is enabled
		if (MainConfig.doFormatChat()) {
			// Check for player mentions
			for (CorePlayer p : CoreProfile.PLAYER_MANAGER) {
				if (msg.toLowerCase().contains(p.getName().toLowerCase())) {

					// play sound for mentioned player
					p.getPlayer().playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, .25F, 2F);

					// replace names with display names
					msg = msg.replaceAll("(?i)" + Pattern.quote(p.getName()), p.getDisplayName() + ChatColor.GRAY);
				}
			}

			event.setFormat(String.format(
					ChatColor.translateAlternateColorCodes('&', RiddlesCore.getSettings().get(player.getLocale(), "chat.format")),
					player.getRank().getDisplayName(),
					player.getDisplayName(),
					msg
			));
		}
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