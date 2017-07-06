/*
 * rv_core
 * 
 * Created on 06 July 2017 at 12:25 PM.
 */

package com.riddlesvillage.core.pgm.match;

import com.riddlesvillage.core.pgm.team.ITeam;
import com.riddlesvillage.core.pgm.team.TeamList;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collections;

public final class Matchup {

    private final TeamList teams = new TeamList();

    public Matchup(final TeamList teams) {
        Validate.notNull(teams);
        this.teams.addAll(teams);
    }

    public Matchup(final ITeam... teams) {
        Validate.noNullElements(teams);
        Collections.addAll(this.teams, teams);
    }

    public TeamList getTeams() {
        return teams;
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
}