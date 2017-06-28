package com.riddlesvillage.core.internal.command;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.internal.config.SpawnsConfig;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.manager.CorePlayerManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class TPSpawnCommand implements CommandExecutor {

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

        if (!player.isMod()) {
            player.sendMessage("player.error.no-permission");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("command.usage", new String[] {"$usage"}, "/tpspawn <name>");
            return true;
        }

        final String spawn = args[0];
        // Obtain location by name from config file
        final Location location = SpawnsConfig.get(spawn);

        if (location == null) {
            player.sendMessage("spawn.absent", new String[] {"$spawn"}, spawn);
            return true;
        }

        player.getPlayer().teleport(location);
        return true;
    }
}