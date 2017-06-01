/*
 * rv_core
 * 
 * Created on 01 June 2017 at 11:42 PM.
 */

package com.riddlesvillage.core.player;

import com.riddlesvillage.core.collect.EnhancedList;

import java.util.Collection;

/**
 * Delegate for {@link EnhancedList<GamePlayer>}
 *
 * @see EnhancedList
 * @see GamePlayer
 */
public class GamePlayerList extends EnhancedList<GamePlayer> {

	public GamePlayerList() {}

	public GamePlayerList(int initialCapacity) {
		super(initialCapacity);
	}

	public GamePlayerList(GamePlayer... elements) {
		super(elements);
	}

	public GamePlayerList(Collection<? extends GamePlayer> c) {
		super(c);
	}
}