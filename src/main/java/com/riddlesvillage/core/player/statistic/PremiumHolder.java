/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.statistic;

import com.riddlesvillage.core.database.Identity;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.OfflineCorePlayer;
import com.riddlesvillage.core.player.event.PremiumStatusModificationEvent;
import com.riddlesvillage.core.player.profile.CoreProfile;
import org.bukkit.Bukkit;

/**
 * Represents any user that can contain the "premium" attribute
 * whether he is online or not.
 *
 * @see CoreProfile
 * @see com.riddlesvillage.core.database.data.DataInfo#PREMIUM
 */
public interface PremiumHolder extends CoreProfile, Identity {


    /**
     * @return  {@code true} if the holder is a premium player,
     *          {@code false} if otherwise.
     */
    default boolean isPremium() {
        CorePlayer player = toCorePlayer();
        if (player == null) {
            return (getUuid() == null ?
                    OfflineCorePlayer.fromName(getName()) :
                    OfflineCorePlayer.fromUuid(getUuid())
            ).isPremium();
        } else {
            return player.isPremium();
        }
    }


    /**
     * @deprecated Used for local storage. Use {@link #setPremium(boolean)} instead
     */
    @Deprecated
    default void _setPremium(final boolean premium) {
        CorePlayer player = toCorePlayer();
        if (player == null) {
            (getUuid() == null ?
                    OfflineCorePlayer.fromName(getName()) :
                    OfflineCorePlayer.fromUuid(getUuid())
            )._setPremium(premium);
        } else {
            player._setPremium(premium);
        }
    }


    /**
     * Calls a new {@link PremiumStatusModificationEvent} event.
     *
     * After it is processed, if the {@code event} has not been
     * cancelled, {@link #_setPremium(boolean)} is called with
     * the {@param premium} parameter and the database
     * is updated with the new value.
     *
     * @param   premium
     *          The new premium value.
     * @see     PremiumStatusModificationEvent
     */
    default void setPremium(final boolean premium) {
        PremiumStatusModificationEvent event = new PremiumStatusModificationEvent(this, premium);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            _setPremium(premium);

            update(DataInfo.PREMIUM, premium);
        }
    }
}