package com.riddlesvillage.core.internal.command;

import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.CorePlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Matthew E on 6/8/2017.
 */
public class BuyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final CorePlayer player = CorePlayerManager.getInstance().get(sender.getName());
        if (player == null) {
            RiddlesCore.log("command.only-players");
            return true;
        }
        return false;
    }
}
