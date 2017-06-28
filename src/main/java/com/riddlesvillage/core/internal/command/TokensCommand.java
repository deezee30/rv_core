package com.riddlesvillage.core.internal.command;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.database.Database;
import com.riddlesvillage.core.database.DatabaseAPI;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.database.data.DataOperator;
import com.riddlesvillage.core.database.value.Value;
import com.riddlesvillage.core.database.value.ValueType;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.OfflineCorePlayer;
import com.riddlesvillage.core.player.event.TokenValueModificationException;
import com.riddlesvillage.core.player.profile.CoreProfile;
import com.riddlesvillage.core.player.statistic.TokensHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public final class TokensCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        final boolean isPlayer = sender instanceof Player;

        switch (args.length) {
            case 0:
                if (!isPlayer) {
                    Core.log("command.usage", new String[] {"$usage"}, "/tokens <name> (<give|take|set> <amount>)");
                    return true;
                }

                CorePlayer senderPlayer = CoreProfile.PLAYER_MANAGER.get(sender.getName());

                senderPlayer.sendMessage("border");
                senderPlayer.sendMessage("tokens.notify-self", new String[] {"$tokens"}, senderPlayer.getTokens());
                senderPlayer.sendMessage("border");

                break;
            case 1:
                String targetName = args[0];
                CorePlayer player = CoreProfile.PLAYER_MANAGER.get(targetName);

                if (player == null) {

                    TokensHolder offlinePlayer = OfflineCorePlayer.fromName(targetName);

                    if (!offlinePlayer.hasPlayed()) {

                        if (isPlayer)
                            CoreProfile.PLAYER_MANAGER.get(sender.getName()).sendMessage(
                                    "player.error.not-found",
                                    new String[] {"$player"},
                                    targetName
                            );
                        else Core.log("player.error.not-found", new String[] {"$player"}, targetName);

                    } else {
                        int tokens = offlinePlayer.getTokens();

                        if (isPlayer) {
                            senderPlayer = CoreProfile.PLAYER_MANAGER.get(sender.getName());

                            senderPlayer.sendMessage("border");
                            senderPlayer.sendMessage(
                                    "tokens.notify-other",
                                    new String[] {"$player", "$tokens"},
                                    targetName,
                                    tokens
                            );
                            senderPlayer.sendMessage("border");
                        } else {
                            Core.log("border");
                            Core.log(
                                    "tokens.notify-other",
                                    new String[] {"$player" , "$tokens"},
                                    targetName,
                                    tokens
                            );
                            Core.log("border");
                        }
                    }
                } else {
                    int tokens = player.getTokens();

                    if (isPlayer) {
                        senderPlayer = CoreProfile.PLAYER_MANAGER.get(sender.getName());

                        senderPlayer.sendMessage("border");
                        senderPlayer.sendMessage(
                                "tokens.notify-other",
                                new String[] {"$player", "$tokens"},
                                player.getDisplayName(),
                                tokens
                        );
                        senderPlayer.sendMessage("border");
                    } else {
                        Core.log("border");
                        Core.log(
                                "tokens.notify-other",
                                new String[] {"$player" , "$tokens"},
                                targetName,
                                tokens
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
                        player.setTokens(value);
                    } catch (TokenValueModificationException e) {
                        error = true;
                        player.sendMessage("Error: " + e.getMessage());
                    }
                } else {
                    OfflineCorePlayer offlinePlayer = OfflineCorePlayer.fromName(targetName);

                    if (offlinePlayer.hasPlayed()) {
                        int newTokens = value.appendTo(offlinePlayer.getTokens());
                        DatabaseAPI.update(
                                Database.getMainCollection(),
                                offlinePlayer.getUuid(),
                                DataOperator.$SET,
                                DataInfo.TOKENS,
                                newTokens,
                                (updateResult, throwable) -> Core.logIf(
                                        !updateResult.wasAcknowledged(),
                                        "Failed updating %s's token value to %s: %s",
                                        offlinePlayer.getName(),
                                        newTokens,
                                        throwable
                                )
                        );
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
                            "tokens." + value.getType().toString().toLowerCase(),
                            new String[] {"$tokens", "$player"},
                            value.getValue(),
                            targetName
                    );
                } else {
                    Core.log(
                            "tokens." + value.getType().toString().toLowerCase(),
                            new String[] {"$tokens" , "$player"},
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