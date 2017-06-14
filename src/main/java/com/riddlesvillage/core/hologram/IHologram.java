package com.riddlesvillage.core.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Matthew E on 6/11/2017.
 */
public interface IHologram {
    Location getLocation();

    List<String> getLineList();

    void hide(Player... players);

    void display(Player... players);
    
    void display(Player[] players, String... replace);
    
    void display(String... replace);

    void display();

    void addLine(String line);
}
