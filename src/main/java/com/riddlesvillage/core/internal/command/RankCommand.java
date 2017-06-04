/*
 * rv_core
 * 
 * Created on 04 June 2017 at 7:11 PM.
 */

package com.riddlesvillage.core.internal.command;

import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.CorePlayerManager;
import com.riddlesvillage.core.player.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Locale;

public final class RankCommand implements CommandExecutor {

	private static final CorePlayerManager MANAGER = CorePlayerManager.getInstance();

	@Override
	public boolean onCommand(CommandSender sender,
							 Command command,
							 String label,
							 String[] args) {
		CorePlayer player = MANAGER.get(sender.getName());
		if (player != null) {
			player.sendMessage("command.only-console");
			return true;
		}

		switch (args.length) {
			default:
				RiddlesCore.log("command.usage", new String[] {"$usage"}, "/rank <player> <rank>");
			case 0:
				break;
			case 2:
				String rankName = args[1].toUpperCase(Locale.ENGLISH);

				try {
					Rank rank = Rank.valueOf(rankName);

					String targetName = args[0];
					MANAGER.getOrOffline(targetName).setRank(rank);
					RiddlesCore.log("%s's rank has been set to %s", targetName, rankName);

					return true;
				} catch (IllegalArgumentException e) {
					RiddlesCore.log("%s is not a valid rank", rankName);
					break;
				}
		}

		RiddlesCore.log("Available ranks: " + Arrays.asList(Rank.values()));

		return true;
	}
}