package com.riddlesvillage.core.commands;

import com.riddlesvillage.core.api.menu.Menu;
import com.riddlesvillage.core.api.menu.item.MenuItemBuilder;
import com.riddlesvillage.core.player.PlayerHandler;
import com.riddlesvillage.core.RiddlesVillageCore;
import com.riddlesvillage.core.api.command.BaseCommand;
import com.riddlesvillage.core.api.menu.MenuBuilder;
import com.riddlesvillage.core.player.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Matthew E on 4/8/2017.
 */
public class CommandStats extends BaseCommand {
    public CommandStats() {
        super("stats");
    }

    @Override
    public boolean execute(Player sender, String[] args) {
        if (args.length == 0) {
            openStats(sender, sender.getName());
            return true;
        } else {
            String name = args[0];
            GamePlayer mcPlayer = PlayerHandler.getHandler().GAMEPLAYERS.get(name);
            if (mcPlayer == null) {
                return true;
            }
            openStats(sender, mcPlayer.getUsername());
            return true;
        }
    }

    private void openStats(Player player, String name) {
        GamePlayer mcPlayer = PlayerHandler.getHandler().GAMEPLAYERS.get(name);
        Menu menu = MenuBuilder.fastMenu(name + " Stats", 9, RiddlesVillageCore.getCore());
        List<String> loreList = new ArrayList<>();
        loreList.add(" ");
        loreList.add(formatLore("Username", name));
        loreList.add(formatLore("Rank", mcPlayer.getRank().getColor() + mcPlayer.getRank().getName()));
        loreList.add(formatLore("Username History", ""));
        for (String username : mcPlayer.getUsernameHistory().stream().filter(s -> !s.equals("")).collect(Collectors.toList())) {
            loreList.add(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + username);
        }
        menu.setItem(5, new MenuItemBuilder()
                .type(Material.SKULL_ITEM)
                .owner(name)
                .data(3)
                .named("&3&l" + name + "'s Stats")
                .lore(loreList)
                .click((player1, clickType) -> {
                    player1.closeInventory();
                    player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l" + name + "'s Stats"));
                    for (String lore : loreList) {
                        if ((lore == null) || (lore.equals(""))) {
                            continue;
                        }
                        player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "  " + lore));
                    }
                }));
        menu.open(player);
    }

    private String formatLore(String s1, String s2) {
        return ChatColor.DARK_AQUA + s1 + ": " +  ChatColor.AQUA + s2;
    }
}
