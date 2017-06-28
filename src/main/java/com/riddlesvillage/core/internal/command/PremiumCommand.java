package com.riddlesvillage.core.internal.command;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.player.profile.AbstractCoreProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static com.riddlesvillage.core.player.CorePlayer.PLAYER_MANAGER;

public final class PremiumCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String useless,
                             String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            if (sender instanceof Player) {
                PLAYER_MANAGER.get(sender.getName()).sendMessage("command.only-console");
            }

            return true;
        }

        switch (args.length) {
            case 1: {
                String target = args[0];
                AbstractCoreProfile profile = PLAYER_MANAGER.getOrOffline(target);

                if (!profile.hasPlayed()) {
                    Core.log("player.error.not-found", new String[] {"$player"}, target);
                    break;
                }

                Core.log(
                        "premium.notify",
                        new String[] {"$user" , "$premium"},
                        target,
                        profile.isPremium()
                );

                break;
            } case 2: {
                String trueFalse = args[1];
                if (trueFalse.equalsIgnoreCase("true") || trueFalse.equalsIgnoreCase("false")) {
                    String target = args[0];
                    AbstractCoreProfile profile = PLAYER_MANAGER.getOrOffline(target);

                    if (!profile.hasPlayed()) {
                        Core.log("player.error.not-found", new String[] {"$player"}, target);
                        break;
                    }

                    if (Boolean.parseBoolean(trueFalse)) {
                        if (profile.isPremium()) {
                            Core.log("premium.already-true", new String[] {"$user"}, profile);
                        } else {
                            profile.setPremium(true);
                            Core.log("premium.promoted", new String[] {"$user"}, target);
                        }
                    } else {
                        if (profile.isPremium()) {
                            profile.setPremium(false);
                            Core.log("premium.demoted", new String[] {"$user"}, target);
                        } else {
                            Core.log("premium.already-false", new String[] {"$user"}, profile);
                        }
                    }

                    break;
                } else {
                    Core.log(
                            "command.usage",
                            new String[] {"$usage"},
                            "/premium <playername> <true|false>"
                    );

                    break;
                }
            } default:
                Core.log(
                        "command.usage",
                        new String[] {"$usage"},
                        "/premium <playername> (<true|false>)"
                );

                break;
        }

        return true;
    }
}