/*
 * rv_core
 * 
 * Created on 03 June 2017 at 7:26 PM.
 */

package com.riddlesvillage.core.player.statistic;

import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.database.Database;
import com.riddlesvillage.core.database.DatabaseAPI;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.database.data.DataOperator;
import com.riddlesvillage.core.database.value.Value;
import com.riddlesvillage.core.player.event.CoinValueModificationEvent;
import com.riddlesvillage.core.player.event.CoinValueModificationException;
import com.riddlesvillage.core.player.profile.CoreProfile;
import com.riddlesvillage.core.util.MathUtil;
import org.bukkit.Bukkit;

/**
 * Represents any user that is of value in terms of coin amount.
 */
public interface CoinsHolder extends CoreProfile {


	/**
	 * @return The amount of coins the profile currently has.
	 */
	int getCoins();


	/**
	 * Sets the local coin value in the holder's class instance.
	 *
	 * No processing needs to be done in this method.  Instead it
	 * is handled by {@link CoinValueModificationEvent} events.
	 *
	 * <b>This method should not be used by external classes</b>
	 *
	 * An example of how this method should look like is as follows:
	 *
	 * <code>
	 * private int coins = 0;
	 *
	 * {@literal @}Override
	 * public void modifyCoins(int coins) {@literal {}
	 *     this.coins = coins;
	 * {@literal }}
	 * </code>
	 *
	 * @param	coins
	 * 			The new coin value.
	 * @see     #setCoins(Value)
	 * @see     CoinValueModificationEvent
	 * @since   1.1
	 * @deprecated Used for local storage. Use {@link #setCoins(Value)} instead
	 */
	void modifyCoins(int coins);


	/**
	 * Calls a new {@link CoinValueModificationEvent} event.
	 *
	 * After it is processed, if the {@code event} has not been
	 * cancelled, {@link #modifyCoins(int)} is called with the
	 * new coin value calculated (via {@link Value#appendTo(int)})
	 * by this method and the database collection is updated with
	 * the new value.
	 *
	 * @param	value
	 * 			The amount of coins being changed along with the
	 * 			change type, {@code GIVE}, {@code SET} or {@code TAKE}.
	 * @throws	CoinValueModificationException
	 * 			If the amount provided ({@param value}) is a
	 *          negative.
	 * @throws	CoinValueModificationException
	 * 			If subtracting the provided amount from the
	 * 			current coin amount will result in a
	 * 			negative.
	 * @see     CoinValueModificationEvent
	 * @see     Value
	 * @since	1.1
	 */
	default void setCoins(Value<Integer> value) throws CoinValueModificationException {
		CoinValueModificationEvent event = new CoinValueModificationEvent(
				this, getCoins(), new Value<>(
						MathUtil.floor((double) value.getValue() * getCoinMultiplier()),
						value.getType())
		);

		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			int newCoins = event.getNewCoins();

			modifyCoins(newCoins);

			DatabaseAPI.update(
					Database.getMainCollection(),
					getUuid(),
					DataOperator.$SET,
					DataInfo.COINS,
					newCoins,
					updateResult -> RiddlesCore.logIf(
							!updateResult.wasAcknowledged(),
							"Failed updating %s's coin value to %s",
							getName(),
							newCoins
					)
			);
		}
	}


	/**
	 * A coin multiplier value for special events or personal perks.
	 *
	 * <b>Default is 1.0</b>
	 *
	 * Applied when adding coins to the current balance. Only values
	 * higher than 0.0 are possible. In case the resulting coin
	 * count is not an integer, it is rounded to the lowest whole
	 * number.
	 *
	 * @return Coin multiplier for specific {@code CoinsHolder}.
	 */
	double getCoinMultiplier();

	/**
	 * A coin multiplier value for special events or personal perks.
	 *
	 * <b>Default is 1.0</b>
	 *
	 * Applied when adding coins to the current balance. Only values
	 * higher than 0.0 are possible. In case the resulting coin
	 * count is not an integer, it is rounded to the lowest whole
	 * number.
	 *
	 * @param factor The factor by which the base coin is multiplied.
	 */
	void setCoinMultiplier(double factor);
}