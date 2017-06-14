package com.riddlesvillage.core.gun;

import org.bukkit.entity.Player;

/**
 * Created by Matthew E on 6/14/2017.
 */
public abstract class Gun {
    private String name;

    public Gun(String name) {
        this.name = name;
    }

    public abstract void fire(Player shooter);
}
