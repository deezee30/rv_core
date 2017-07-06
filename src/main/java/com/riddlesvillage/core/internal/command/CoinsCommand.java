package com.riddlesvillage.core.internal.command;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.database.value.Value;
import com.riddlesvillage.core.database.value.ValueType;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.OfflineCorePlayer;
import com.riddlesvillage.core.player.event.CoinValueModificationException;
import com.riddlesvillage.core.player.profile.CoreProfile;
import com.riddlesvillage.core.player.statistic.CoinsHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public final class CoinsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        final boolean isPlayer = sender instanceof Player;

        switch (args.length) {
            case 0:
                if (!isPlayer) {
                    Core.log("command.usage", new String[] {"$usage"}, "/coins <name> (<give|take|set> <amount>)");
                    return true;
                }

                CorePlayer senderPlayer = CoreProfile.PLAYER_MANAGER.get(sender.getName());

                senderPlayer.sendMessage("border");
                senderPlayer.sendMessage("coins.notify-self", new String[] {"$coins"}, senderPlayer.getCoins());
                senderPlayer.sendMessage("border");

                break;
            case 1:
                String targetName = args[0];
                CorePlayer player = CoreProfile.PLAYER_MANAGER.get(targetName);

                if (player == null) {

                    CoinsHolder offlinePlayer = OfflineCorePlayer.fromName(targetName);

                    if (!offlinePlayer.hasPlayed()) {

                        if (isPlayer)
                            CoreProfile.PLAYER_MANAGER.get(sender.getName()).sendMessage(
                                    "player.error.not-found",
                                    new String[] {"$player"},
                                    targetName
                            );
                        else Core.log("player.error.not-found", new String[] {"$player"}, targetName);

                    } else {
                        int coins = offlinePlayer.getCoins();

                        if (isPlayer) {
                            senderPlayer = CoreProfile.PLAYER_MANAGER.get(sender.getName());

                            senderPlayer.sendMessage("border");
                            senderPlayer.sendMessage(
                                    "coins.notify-other",
                                    new String[] {"$player", "$coins"},
                                    targetName,
                                    coins
                            );
                            senderPlayer.sendMessage("border");
                        } else {
                            Core.log("border");
                            Core.log(
                                    "coins.notify-other",
                                    new String[] {"$player" , "$coins"},
                                    targetName,
                                    coins
                            );
                            Core.log("border");
                        }
                    }
                } else {
                    int coins = player.getCoins();

                    if (isPlayer) {
                        senderPlayer = CoreProfile.PLAYER_MANAGER.get(sender.getName());

                        senderPlayer.sendMessage("border");
                        senderPlayer.sendMessage(
                                "coins.notify-other",
                                new String[] {"$player", "$coins"},
                                player.getDisplayName(),
                                coins
                        );
                        senderPlayer.sendMessage("border");
                    } else {
                        Core.log("border");
                        Core.log(
                                "coins.notify-other",
                                new String[] {"$player" , "$coins"},
                                targetName,
                                coins
                        );
                        Core.log("border");
                    }
                }

                break;
            case 3:
                if (isPlayer && !CoreProfile.PLAYER_MANAGER.get(sender.getName()).isAdmin()) {
                    CoreProfile.PLAYER_MANAGER.get(sender.getName()).sendMessage("command.not-found");
                    break;
                }

                targetName = args[0];
                String giveSetTake = args[1].toLowerCase(Locale.ENGLISH);
                int amount = -1;

                if (!(giveSetTake.equals("give") || giveSetTake.equals("take") || giveSetTake.equals("set"))) {
                    if (isPlayer) CoreProfile.PLAYER_MANAGER.get(sender.getName()).sendMessage("command.not-found");
                    else Core.log("command.not-found");
                    break;
                }

                try {
                    amount = Math.abs(Integer.parseInt(args[2]));
                } catch (NumberFormatException e) {
                    if (isPlayer) CoreProfile.PLAYER_MANAGER.get(sender.getName()).sendMessage("command.invalid-value");
                    else Core.log("command.invalid-value");
                }

                if (amount == -1) return true;

                Value<Integer> value = new Value<>(amount, ValueType.valueOf(giveSetTake.toUpperCase(Locale.ENGLISH)));
                boolean error = false;

                player = CoreProfile.PLAYER_MANAGER.get(targetName);
                if (player != null) {
                    try {
                        player.setCoins(value);
                    } catch (CoinValueModificationException e) {
                        error = true;
                        player.sendMessage("Error: " + e.getMessage());
                    }
                } else {
                    OfflineCorePlayer offlinePlayer = OfflineCorePlayer.fromName(targetName);

                    if (offlinePlayer.hasPlayed()) {
                        int newCoins = value.appendTo(offlinePlayer.getCoins());
                        offlinePlayer.update(DataInfo.COINS, newCoins);
                    } else {
                        if (isPlayer)
                            CoreProfile.PLAYER_MANAGER.get(sender.getName()).sendMessage(
                                    "player.error.not-found",
                                    new String[] {"$player"},
                                    targetName
                            );
                        else Core.log("player.error.not-found", new String[] {"$player"}, targetName);

                        return true;
                    }
                }

                if (error) {
                    break;
                }

                if (isPlayer) {
                    CoreProfile.PLAYER_MANAGER.get(sender.getName()).sendMessage(
                            "coins." + value.getType().toString().toLowerCase(),
                            new String[] {"$coins", "$player"},
                            value.getValue(),
                            targetName
                    );
                } else {
                    Core.log(
                            "coins." + value.getType().toString().toLowerCase(),
                            new String[] {"$coins" , "$player"},
                            value.getValue(),
                            targetName
                    );
                }

                break;
            default:
                if (isPlayer) CoreProfile.PLAYER_MANAGER.get(sender.getName()).sendMessage("command.not-found");
                else Core.log("command.not-found");

                break;
        }

        return true;
    }
}