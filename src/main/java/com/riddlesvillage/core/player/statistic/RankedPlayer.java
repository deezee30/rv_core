/*
 * rv_core
 * 
 * Created on 05 June 2017 at 9:56 PM.
 */

package com.riddlesvillage.core.player.statistic;

import com.riddlesvillage.core.player.EnumRank;

public interface RankedPlayer {

	EnumRank getRank();

	default boolean isAllowedFor(EnumRank rank) {
		return getRank().getId() >= rank.getId();
	}
}