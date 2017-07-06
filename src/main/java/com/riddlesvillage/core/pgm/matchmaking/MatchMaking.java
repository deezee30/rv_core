/*
 * rv_core
 * 
 * Created on 06 July 2017 at 12:23 PM.
 */

package com.riddlesvillage.core.pgm.matchmaking;

public interface MatchMaking<K, V> {
    boolean matches(K k, V v);
}