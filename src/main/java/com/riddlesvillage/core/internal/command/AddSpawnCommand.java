package com.riddlesvillage.core.internal.command;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.internal.config.SpawnsConfig;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.manager.CorePlayerManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public final class AddSpawnCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender,
							 Command command,
							 String label,
							 String[] args) {

		final CorePlayer player = CorePlayerManager.getInstance().get(sender.getName());
		if (player == null) {
			Core.log("command.only-players");
			return true;
		}

		if (!player.isAdmin()) {
			player.sendMessage("player.error.no-permission");
			return true;
		}

		if (args.length != 1) {
			player.sendMessage("command.usage", new String[] {
					"$usage"
			},		"/addspawn <name>");
			return true;
		}

		final String spawn = args[0];

		try {
			Location location = player.getLocation();
			SpawnsConfig.save(spawn, location);
			player.sendMessage("spawn.add", new String[] {
					"$spawn",	"$world"
			},		spawn,		location.getWorld().getName());
		} catch (IOException e) {
			player.sendMessage(e.getMessage());
		}

		return true;
	}
}