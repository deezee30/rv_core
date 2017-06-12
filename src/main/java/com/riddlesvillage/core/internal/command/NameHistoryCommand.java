package com.riddlesvillage.core.internal.command;

import com.google.common.collect.ImmutableList;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.manager.CorePlayerManager;
import com.riddlesvillage.core.player.OfflineCorePlayer;
import com.riddlesvillage.core.player.profile.AbstractCoreProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Matthew E on 6/7/2017.
 */
public class NameHistoryCommand implements CommandExecutor {

    private static final String ERROR = "player.error.not-found";
    private static final String USAGE = "/nameshistory | /nameshistory <player>";
    private static final CorePlayerManager MANAGER = CorePlayer.PLAYER_MANAGER;

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        final boolean isPlayer = sender instanceof Player;
        final CorePlayer playerSender = MANAGER.get(sender.getName());

        switch (args.length) {
            case 0:
                if (!RiddlesCore.logIf(!isPlayer, "command.only-players")) {
                    ImmutableList<String> l = playerSender.getStatisticValues();
                    playerSender.sendMessages(l.toArray(new String[l.size()]));
                }
                break;
            case 1:
                String victimName = args[0];

                CorePlayer victim = MANAGER.get(victimName);
                if (victim != null) {
                    ImmutableList<String> l = victim.getStatisticValues();
                    playerSender.sendMessages(l.toArray(new String[l.size()]));
                } else {
                    AbstractCoreProfile profile = OfflineCorePlayer.fromName(args[0]);
                    if (profile.hasPlayed()) {
                        List<String> l = profile.getNameHistory();
                        if (isPlayer) {
                            playerSender.sendMessages(l.toArray(new String[l.size()]));
                        } else {
                            l.forEach(s -> Messaging.log(s));
                        }
                    } else {
                        if (!RiddlesCore.logIf(!isPlayer, ERROR)) {
                            playerSender.sendMessage(ERROR);
                        }
                    }
                }
                break;
            default:
                if (!RiddlesCore.logIf(!isPlayer, "command.usage", new String[]{"$usage"}, USAGE)) {
                    playerSender.sendMessage("command.usage", new String[]{"$usage"}, USAGE);
                }
        }
        return true;
    }
}
