package com.riddlesvillage.core.title;

import com.riddlesvillage.core.util.TitleAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Matthew E on 6/14/2017.
 */
public class Title {
    protected String title;
    protected String subTitle;
    protected int fadeIn;
    protected int fadeOut;
    protected int duration;

    protected Title(String title, String subTitle, int fadeIn, int fadeOut, int duration) {
        this.title = title;
        this.subTitle = subTitle;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.duration = duration;
    }

    public void send(Player... players) {
        for (Player player : players) {
            TitleAPI.sendFullTitle(player, fadeIn, duration, fadeOut, title, subTitle);
        }
    }

    public void send(Player player) {
        TitleAPI.sendFullTitle(player, fadeIn, duration, fadeOut, title, subTitle);
    }

    public void send() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            TitleAPI.sendFullTitle(player, fadeIn, duration, fadeOut, title, subTitle);
        }
    }
}
