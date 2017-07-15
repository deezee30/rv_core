/*
 * rv_core
 * 
 * Created on 05 June 2017 at 9:56 PM.
 */

package com.riddlesvillage.core.player.statistic;

import com.riddlesvillage.core.database.Database;
import com.riddlesvillage.core.database.Identity;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.OfflineCorePlayer;
import com.riddlesvillage.core.player.Rank;
import com.riddlesvillage.core.player.profile.CoreProfile;

/**
 * Represents any profile that can hold and modify a {@link Rank}.
 * @see Rank
 */
public interface RankedPlayer extends CoreProfile, Identity {

    /**
     * @return the holder's current cached rank
     */
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
     * @deprecated For internal use. Use {@link #setRank(Rank)} instead
     */
    @Deprecated
    default void _setRank(final Rank rank) {
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

    /**
     * Sets the holder's {@link Rank} to the specified parameter
     * in cache and also updates the {@link Database#getMainPlayerCollection()}
     * in the database.
     *
     * @param   rank the rank to update to
     * @see     Rank
     */
    default void setRank(final Rank rank) {
        _setRank(rank);

        update(DataInfo.RANK, rank);
    }

    /**
     * Returns if the rank holder is allowed to perform
     * a task that requires to be of the provided
     * <b>or higher.</b>
     *
     * @param   rank the rank to check
     * @return  if the rank provided is greater than or
     *          equal to the holder's rank
     * @see     Rank
     */
    default boolean isAllowedFor(final Rank rank) {
        return getRank().getId() >= rank.getId();
    }
}