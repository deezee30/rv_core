package com.riddlesvillage.core.internal.command;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.Rank;
import com.riddlesvillage.core.player.manager.CorePlayerManager;
import com.riddlesvillage.core.player.profile.AbstractCoreProfile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Matthew E on 6/14/2017.
 */
public class TeleportCommand  implements CommandExecutor {

    private static final String             ERROR   = "player.error.not-found";
    private static final String             USAGE   = "/teleport <player>";
    private static final CorePlayerManager MANAGER = CorePlayer.PLAYER_MANAGER;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final boolean isPlayer = sender instanceof Player;
        final CorePlayer playerSender = MANAGER.get(sender.getName());

        switch (args.length) {
            case 0:
             Core.logIf(!isPlayer, "command.only-players");
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

                if (!victim.isOnline()) {
                    playerSender.sendMessage(ERROR, new String[] {"$player"}, victimName);
                    return true;
                }
                if (playerSender.isAllowedFor(Rank.DEV)) {
                    playerSender.getPlayer().teleport(Bukkit.getPlayer(victim.getUuid()));
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
