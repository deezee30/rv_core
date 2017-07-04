/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.statistic;

import com.riddlesvillage.core.database.Identity;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.database.value.Value;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.OfflineCorePlayer;
import com.riddlesvillage.core.player.event.TokenValueModificationEvent;
import com.riddlesvillage.core.player.event.TokenValueModificationException;
import com.riddlesvillage.core.player.profile.CoreProfile;
import org.bukkit.Bukkit;

/**
 * Represents any user that is of value in terms of token amount.
 */
public interface TokensHolder extends CoreProfile, Identity {


    /**
     * @return The amount of tokens the profile currently has.
     */
    default int getTokens() {
        CorePlayer player = toCorePlayer();
        if (player == null) {
            return (getUuid() == null ?
                    OfflineCorePlayer.fromName(getName()) :
                    OfflineCorePlayer.fromUuid(getUuid())
            ).getTokens();
        } else {
            return player.getTokens();
        }
    }


    /**
     * @deprecated Used for local storage. Use {@link #setTokens(Value)} instead
     */
    @Deprecated
    default void _setTokens(final int tokens) {
        CorePlayer player = toCorePlayer();
        if (player == null) {
            (getUuid() == null ?
                    OfflineCorePlayer.fromName(getName()) :
                    OfflineCorePlayer.fromUuid(getUuid())
            )._setTokens(tokens);
        } else {
            player._setTokens(tokens);
        }
    }


    /**
     * Calls a new {@link TokenValueModificationEvent} event.
     *
     * After it is processed, if the {@code event} has not been
     * cancelled, {@link #_setTokens(int)} is called with the
     * new token value calculated (via {@link Value#appendTo(int)})
     * by this method and the database collection is updated with
     * the new value.
     *
     * @param   value
     *          The amount of tokens being changed along with the
     *          change type, {@code GIVE}, {@code SET} or {@code TAKE}.
     * @throws  TokenValueModificationException
     *          If the amount provided ({@param value}) is a
     *          negative.
     * @throws  TokenValueModificationException
     *          If subtracting the provided amount from the
     *          current token amount will result in a
     *          negative.
     * @see     TokenValueModificationEvent
     * @see     Value
     */
    default void setTokens(final Value<Integer> value) throws TokenValueModificationException {
        TokenValueModificationEvent event = new TokenValueModificationEvent(this, getTokens(), value);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            int newTokens = event.getNewTokens();

            _setTokens(newTokens);

            update(DataInfo.TOKENS, newTokens);
        }
    }
}