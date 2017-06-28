package com.riddlesvillage.core.internal.command;

import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.manager.CorePlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class DebugCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        boolean debugging = !Messaging.debugEnabled();

        if (sender instanceof Player) {
            CorePlayer player = CorePlayerManager.getInstance().get(sender.getName());
            if (!player.isAdmin()) {
                player.sendMessage("player.error.no-permission");
                return true;
            }

            player.sendMessage("Debugging has been temporarily %s", debugging ? "enabled" : "disabled");
        }

        Messaging.enableDebugging(debugging);
        return true;
    }
}