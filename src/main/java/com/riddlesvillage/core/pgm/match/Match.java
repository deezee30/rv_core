/*
 * rv_core
 * 
 * Created on 06 July 2017 at 12:38 PM.
 */

package com.riddlesvillage.core.pgm.match;

import com.riddlesvillage.core.pgm.Arena;
import com.riddlesvillage.core.pgm.Game;
import com.riddlesvillage.core.pgm.event.MatchChangeStateEvent;
import com.riddlesvillage.core.pgm.player.GamePlayer;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;

import java.util.Iterator;

public final class Match implements Iterable<GamePlayer> {

    private final Game game;
    private final Matchup matchup;
    private final Arena arena;

    private MatchState state;

    public Match(final Game game,
                 final Matchup matchup,
                 final Arena arena) {
        this.game = game;
        this.matchup = Validate.notNull(matchup);
        this.arena = Validate.notNull(arena);
    }

    public void start() {

    }

    public void finish() {

    }

    public Game getGame() {
        return game;
    }

    public Matchup getMatchup() {
        return matchup;
    }

    public Arena getArena() {
        return arena;
    }

    public MatchState getState() {
        return state;
    }

    public void setState(final MatchState state) {
        if (state == null
                || this.state == state
                || !this.state.getClass().isInstance(state)
                || !state.getClass().isInstance(this.state)
                || state.equals(state.getPrevious())
                || state.equals(state.getNext())) {
            return;
        }

        Bukkit.getPluginManager().callEvent(
                new MatchChangeStateEvent(this, this.state, state));
        this.state = state;
    }

    @Override
    public Iterator<GamePlayer> iterator() {
        return matchup.iterator();
    }
}