/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player;

import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.player.profile.CoreProfileList;

import java.util.Collection;

/**
 * Delegate for {@code EnhancedList<CorePlayer>}
 *
 * @see	EnhancedList
 * @see CorePlayer
 * @see CoreProfileList
 */
public class CorePlayerList extends EnhancedList<CorePlayer> {

	public CorePlayerList() {}

	public CorePlayerList(int initialCapacity) {
		super(initialCapacity);
	}

	public CorePlayerList(CorePlayer... elements) {
		super(elements);
	}

	public CorePlayerList(Collection<? extends CorePlayer> c) {
		super(c);
	}
}