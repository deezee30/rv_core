/*
 * rv_core
 * 
 * Created on 05 June 2017 at 9:56 PM.
 */

package com.riddlesvillage.core.player.statistic;

import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.database.Database;
import com.riddlesvillage.core.database.DatabaseAPI;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.database.data.DataOperator;
import com.riddlesvillage.core.player.EnumRank;
import com.riddlesvillage.core.player.profile.CoreProfile;

public interface RankedPlayer extends CoreProfile {

	EnumRank getRank();

	/**
	 * @deprecated Use {@link #setRank(EnumRank)} instead
	 */
	void modifyRank(EnumRank rank);

	default void setRank(EnumRank rank) {
		modifyRank(rank);

		DatabaseAPI.update(
				Database.getMainCollection(),
				getUuid(),
				DataOperator.$SET,
				DataInfo.RANK,
				rank.getName(),
				updateResult -> RiddlesCore.logIf(
						!updateResult.wasAcknowledged(),
						"%s's rank update to %s was unacknowledged",
						getName(),
						rank.getDisplayName()
				)
		);
	}

	default boolean isAllowedFor(EnumRank rank) {
		return getRank().getId() >= rank.getId();
	}
}