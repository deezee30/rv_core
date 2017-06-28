/*
 * rv_core
 * 
 * Created on 03 June 2017 at 10:45 PM.
 */

package com.riddlesvillage.core.internal.command;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.internal.config.MainConfig;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.manager.CorePlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class ClearChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        final CorePlayer player = CorePlayerManager.getInstance().get(sender.getName());
        if (player == null) {
            clearChat(sender);
            return true;
        }

        if (!player.isAdmin()) {
            player.sendMessage("player.error.no-permission");
            return true;
        }

        if (args.length != 0) {
            player.sendMessage("command.usage", new String[] {
                    "$usage"
            }, "/clearchat");
            return true;
        }

        clearChat(sender);
        return true;
    }

    private void clearChat(CommandSender sender) {
        for (int i = 0; i < MainConfig.getClearChatLines(); i++) {
            Core.broadcast("~");
        }

        Core.broadcast("chat.clear", new String[] {"$player"}, sender.getName());
    }
}