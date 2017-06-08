/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.statistic;

import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.database.Database;
import com.riddlesvillage.core.database.DatabaseAPI;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.database.data.DataOperator;
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
public interface TokensHolder extends CoreProfile {


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
	 * Sets the local token value in the holder's class instance.
	 *
	 * No processing needs to be done in this method.  Instead it
	 * is handled by {@link TokenValueModificationEvent} events.
	 *
	 * <b>This method should not be used by external classes</b>
	 *
	 * An example of how this method should look like is as follows:
	 * <code>
	 * private int tokens = 0;
	 *
	 * {@literal @}Override
	 * public void _setTokens(int tokens) {@literal {}
	 *     this.tokens = tokens;
	 * {@literal }}
	 * </code>
	 *
	 * @param   tokens
	 * 			The new token value.
	 * @see		#setTokens(Value)
	 * @see		TokenValueModificationEvent
	 * @since	1.1
	 * @deprecated Used for local storage. Use {@link #setTokens(Value)} instead
	 */
	default void _setTokens(int tokens) {
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
	 * @param	value
	 * 			The amount of tokens being changed along with the
	 * 			change type, {@code GIVE}, {@code SET} or {@code TAKE}.
	 * @throws TokenValueModificationException
	 * 			If the amount provided ({@param value}) is a
	 * 			negative.
	 * @throws	TokenValueModificationException
	 * 			If subtracting the provided amount from the
	 * 			current token amount will result in a
	 * 			negative.
	 * @see		TokenValueModificationEvent
	 * @see		Value
	 * @since	1.1
	 */
	default void setTokens(Value<Integer> value) throws TokenValueModificationException {
		TokenValueModificationEvent event = new TokenValueModificationEvent(this, getTokens(), value);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			int newTokens = event.getNewTokens();

			_setTokens(newTokens);

			DatabaseAPI.update(
					Database.getMainCollection(),
					getUuid(),
					DataOperator.$SET,
					DataInfo.TOKENS,
					newTokens,
					(updateResult, throwable) -> RiddlesCore.logIf(
							!updateResult.wasAcknowledged(),
							"Failed updating %s's token value to %s: %s",
							getName(),
							newTokens,
							throwable
					)
			);
		}
	}
}