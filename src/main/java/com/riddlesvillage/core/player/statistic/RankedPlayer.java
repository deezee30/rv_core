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
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.Rank;
import com.riddlesvillage.core.player.OfflineCorePlayer;
import com.riddlesvillage.core.player.profile.CoreProfile;

public interface RankedPlayer extends CoreProfile {

	default Rank getRank() {
		CorePlayer player = toCorePlayer();
		if (player == null) {
			return (getUuid() == null ?
					OfflineCorePlayer.fromName(getName()) :
					OfflineCorePlayer.fromUuid(getUuid())
			).getRank();
		} else {
			return player.getRank();
		}
	}

	/**
	 * @deprecated Use {@link #setRank(Rank)} instead
	 */
	default void _setRank(Rank rank) {
		CorePlayer player = toCorePlayer();
		if (player == null) {
			(getUuid() == null ?
				OfflineCorePlayer.fromName(getName()) :
					OfflineCorePlayer.fromUuid(getUuid())
			)._setRank(rank);
		} else {
			player._setRank(rank);
		}
	}

	default void setRank(Rank rank) {
		_setRank(rank);

		DatabaseAPI.update(
				Database.getMainCollection(),
				getUuid(),
				DataOperator.$SET,
				DataInfo.RANK,
				rank,
				(updateResult, throwable) -> RiddlesCore.logIf(
						!updateResult.wasAcknowledged(),
						"%s's rank update to %s was unacknowledged: %s",
						getName(),
						rank.getDisplayName(),
						throwable
				)
		);
	}

	default boolean isAllowedFor(Rank rank) {
		return getRank().getId() >= rank.getId();
	}
}