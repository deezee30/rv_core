package com.riddlesvillage.core.internal.command;

import com.google.common.collect.ImmutableList;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.manager.CorePlayerManager;
import com.riddlesvillage.core.player.profile.AbstractCoreProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class StatsCommand implements CommandExecutor {

	private static final String             ERROR   = "player.error.not-found";
	private static final String             USAGE   = "/stats | /stats <player>";
	private static final CorePlayerManager MANAGER = CorePlayer.PLAYER_MANAGER;

	@Override
	public boolean onCommand(CommandSender sender,
							 Command command,
							 String label,
							 String[] args) {
		final boolean isPlayer = sender instanceof Player;
		final CorePlayer playerSender = MANAGER.get(sender.getName());

		switch (args.length) {
			case 0:
				if (!Core.logIf(!isPlayer, "command.only-players")) {
					ImmutableList<String> l = playerSender.getStatisticValues();
					playerSender.sendMessages(l.toArray(new String[l.size()]));
				}
				break;

			case 1:
				String victimName = args[0];

				AbstractCoreProfile victim = MANAGER.getOrOffline(victimName);
				if (!victim.hasPlayed()) {
					if (!Core.logIf(!isPlayer, ERROR, new String[] {"$player"}, victimName)) {
						playerSender.sendMessage(ERROR, new String[] {"$player"}, victimName);
					}

					return true;
				}

				ImmutableList<String> l = victim.getStatisticValues();
				if (isPlayer) {
					playerSender.sendMessages(l.toArray(new String[l.size()]));
				} else {
					l.forEach(s -> Messaging.log(s));
				}

				break;

			default:
				if (!Core.logIf(!isPlayer, "command.usage", new String[] {"$usage"}, USAGE)) {
					playerSender.sendMessage("command.usage", new String[] {"$usage"}, USAGE);
				}
		}
		return true;
	}
}