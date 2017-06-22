/*
 * rv_core
 * 
 * Created on 04 June 2017 at 3:58 PM.
 */

package com.riddlesvillage.core.internal.command;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.manager.CorePlayerManager;
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
		boolean isPlayer = player != null;

		if (isPlayer && !player.isMod()) {
			player.sendMessage("player.error.no-permission");
			return true;
		}

		switch (args.length) {
			default:
				if (isPlayer)	player.sendMessage("command.usage", new String[] {"usage"}, "/god (<player>)");
				else			Core.log("command.usage", new String[] {"usage"}, "/god <player>");
				return true;
			case 0:
				if (!isPlayer)	Core.log("command.usage", new String[] {"usage"}, "/god <player>");
				else {
					player.sendMessage("god." + (player.setDamageable(!player.isDamageable()) ? "disable" : "enable"));
				}

				return true;
			case 1:
				String targetName = args[0];
				CorePlayer target = CorePlayerManager.getInstance().get(targetName);
				if (target == null) {
					if (isPlayer)	player.sendMessage("player.error.not-found", new String[]{"$player"}, targetName);
					else			Core.log("player.error.not-found", new String[] {"$player"}, targetName);
					return true;
				}

				boolean damageable = !target.isDamageable();
				target.setDamageable(damageable);

				String msg = "god." + (damageable ? "disable" : "enable");

				target.sendMessage(msg);
				if (isPlayer)	player.sendMessage(msg);
				else			Core.log(msg);
		}

		return true;
	}
}