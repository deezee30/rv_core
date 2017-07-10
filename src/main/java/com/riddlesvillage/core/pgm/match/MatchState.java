/*
 * rv_core
 * 
 * Created on 10 July 2017 at 3:59 AM.
 */

package com.riddlesvillage.core.pgm.match;

public interface MatchState {

    MatchState getPrevious();

    MatchState getNext();
}