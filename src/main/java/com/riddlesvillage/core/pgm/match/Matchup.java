/*
 * rv_core
 * 
 * Created on 06 July 2017 at 12:25 PM.
 */

package com.riddlesvillage.core.pgm.match;

import com.google.common.collect.Iterators;
import com.riddlesvillage.core.pgm.player.GamePlayer;
import com.riddlesvillage.core.pgm.team.ComparableType;
import com.riddlesvillage.core.pgm.team.Team;
import com.riddlesvillage.core.pgm.team.TeamList;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collections;
import java.util.Iterator;

public final class Matchup implements Iterable<GamePlayer> {

    private final TeamList teams = new TeamList();

    public Matchup(final TeamList teams) {
        Validate.notNull(teams);
        this.teams.addAll(teams);
    }

    public Matchup(final Team... teams) {
        Validate.noNullElements(teams);
        Collections.addAll(this.teams, teams);
    }

    public TeamList getTeams() {
        return teams;
    }

    public boolean smartBalanceTeams(ComparableType... comparables) {
        // TODO: Balance teams by Comparability (Comparable)
        // return true if balanced, false if not
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Matchup matchup = (Matchup) o;

        return new EqualsBuilder()
                .append(teams, matchup.teams)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(teams)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("teams", teams.toString())
                .toString();
    }

    @Override
    public Iterator<GamePlayer> iterator() {
        Iterator<GamePlayer> iterator = Collections.emptyListIterator();
        for (Team team : teams) {
            iterator = Iterators.concat(iterator, team.iterator());
        }

        return iterator;
    }
}