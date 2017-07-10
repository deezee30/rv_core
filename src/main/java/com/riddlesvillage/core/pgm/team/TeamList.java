/*
 * rv_core
 * 
 * Created on 06 July 2017 at 12:26 PM.
 */

package com.riddlesvillage.core.pgm.team;

import com.riddlesvillage.core.collect.EnhancedList;

import java.util.Collection;

public class TeamList extends EnhancedList<Team> {

    public TeamList() {}

    public TeamList(int initialCapacity) {
        super(initialCapacity);
    }

    public TeamList(Team... elements) {
        super(elements);
    }

    public TeamList(Collection<? extends Team> c) {
        super(c);
    }
}