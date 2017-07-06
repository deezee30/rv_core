/*
 * rv_core
 * 
 * Created on 06 July 2017 at 12:28 PM.
 */

package com.riddlesvillage.core.pgm;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.collect.EnhancedList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

public final class GameManager implements Iterable<Game> {

    private static final GameManager instance = new GameManager();
    private static final EnhancedList<Game> games = new EnhancedList<>();

    private GameManager() {}

    public boolean register(final JavaPlugin plugin,
                            final Game game) {
        if (plugin == null || game == null)
            return false;

        game.plugin = plugin;
        games.add(game);

        Core.log("Hooked into game `%s` from plugin `%s`", game.name, plugin.getName());

        // success
        return true;
    }

    public boolean unRegister(final Game game) {
        return Core.logIf(games.removeIf(game != null, game), "Unhooked `%s`", game.name);
    }

    @Override
    public Iterator<Game> iterator() {
        return games.iterator();
    }

    public static GameManager getInstance() {
        return instance;
    }
}