/*
 * rv_core
 * 
 * Created on 04 June 2017 at 4:16 PM.
 */

package com.riddlesvillage.core.internal.command;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.manager.CorePlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class VanishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        CorePlayer player = CorePlayerManager.getInstance().get(sender.getName());
        if (player == null) {
            Core.log("command.only-players");
            return true;
        }

        if (!player.isAdmin()) {
            player.sendMessage("player.error.no-permission");
            return true;
        }

        if (args.length != 0) {
            player.sendMessage("command.usage", new String[]{"$usage"}, "/vanish");
            return true;
        }

        player.toggleVanish(false);

        return true;
    }
}