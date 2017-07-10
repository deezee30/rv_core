/*
 * rv_core
 * 
 * Created on 06 July 2017 at 12:26 PM.
 */

package com.riddlesvillage.core.pgm.kit;

import com.riddlesvillage.core.collect.EnhancedList;

import java.util.Collection;

public class KitList extends EnhancedList<Kit> {

    public KitList() {}

    public KitList(int initialCapacity) {
        super(initialCapacity);
    }

    public KitList(Kit... elements) {
        super(elements);
    }

    public KitList(Collection<? extends Kit> c) {
        super(c);
    }
}