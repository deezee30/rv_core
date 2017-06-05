/*
 * rv_core
 * 
 * Created on 04 June 2017 at 3:58 PM.
 */

package com.riddlesvillage.core.internal.command;

import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.CorePlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class GodCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender,
							 Command command,
							 String label,
							 String[] args) {

		CorePlayer player = CorePlayerManager.getInstance().get(sender.getName());
		boolean isPlayer = player == null;

		if (!isPlayer || (isPlayer && player.isAdmin())) {
			player.sendMessage("player.error.no-permission");
			return true;
		}

		switch (args.length) {
			default:
				if (isPlayer)	player.sendMessage("command.usage", new String[] {"usage"}, "/god (<player>)");
				else			RiddlesCore.log("command.usage", new String[] {"usage"}, "/god <player>");
				return true;
			case 0:
				if (!isPlayer)	RiddlesCore.log("command.usage", new String[] {"usage"}, "/god <player>");
				else {
					player.sendMessage("god." + (player.setDamageable(!player.isDamageable()) ? "disabled" : "enabled"));
				}

				return true;
			case 1:
				String targetName = args[0];
				CorePlayer target = CorePlayerManager.getInstance().get(targetName);
				if (target == null) {
					if (isPlayer)	player.sendMessage("player.error.not-found", new String[]{"$player"}, targetName);
					else			RiddlesCore.log("player.error.not-found", new String[]{"$player"}, targetName);
					return true;
				}

				boolean damageable = !target.isDamageable();
				target.setDamageable(damageable);
				target.sendMessage("god." + (damageable ? "disabled" : "enabled"));
				player.sendMessage("god." + (damageable ? "disabled" : "enabled"));
		}

		return true;
	}
}