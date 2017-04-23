package com.riddlesvillage.core.player.rank;

import com.riddlesvillage.core.player.GamePlayer;
import com.riddlesvillage.core.player.PlayerHandler;
import org.bukkit.entity.Player;

/**
 * Created by matt1 on 3/6/2017.
 */
public enum Rank {

    DEFAULT(0, "Default", "&7%name%&7: &7", "&7", "&7"),
    HELPER(2, "Helper", "&5[Helper] &5%s &f: %s", "&5", "&5[Helper]"),
    ADMIN(10, "Admin", "&c[&lAdmin&c] &c%name%&f: ", "&c", "&c[Admin]&c"),
    LEAD_DEVELOPER(11, "LeadDeveloper", "&3&lLead Developer &b%name%&f: ", "&3", "&3[LEAD DEV]"),
    OWNER(12, "Owner", "&4[&lOwner&4] &4%name%&f: ", "&4", "&4[Owner]&4"),;

    private int id;
    private String name;
    private String format;
    private String color;
    private String tabName;

    Rank(int id, String name, String format, String color, String tabName) {
        this.id = id;
        this.name = name;
        this.format = format;
        this.color = color;
        this.tabName = tabName;
    }

    public static boolean isAdmin(Player player) {
        GamePlayer gamePlayer =  PlayerHandler.getHandler().GAMEPLAYERS.get(player.getName());
        if (gamePlayer  != null) {
            return gamePlayer.getRank().getId() >= ADMIN.getId();
        }
        return false;
    }


    public String getColor() {
        return color;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    public String format(String name, String msg) {
        return this.format.replaceAll("%name%", name).replaceAll("%message%", msg).replaceAll("%tags%", "");
    }

    public String getTabName() {
        return tabName;
    }
}
