/*
 * rv_core
 * 
 * Created on 03 June 2017 at 7:26 PM.
 */

package com.riddlesvillage.core.player.statistic;

import com.riddlesvillage.core.database.Identity;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.database.value.Value;
import com.riddlesvillage.core.database.value.ValueType;
import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.OfflineCorePlayer;
import com.riddlesvillage.core.player.event.CoinValueModificationEvent;
import com.riddlesvillage.core.player.event.CoinValueModificationException;
import com.riddlesvillage.core.player.profile.CoreProfile;
import com.riddlesvillage.core.util.MathUtil;
import org.bukkit.Bukkit;

/**
 * Represents any user that is of value in terms of coin amount.
 */
public interface CoinsHolder extends CoreProfile, Identity {


    /**
     * @return The amount of coins the profile currently has.
     */
    default int getCoins() {
        CorePlayer player = toCorePlayer();
        if (player == null) {
            return (getUuid() == null ?
                    OfflineCorePlayer.fromName(getName()) :
                    OfflineCorePlayer.fromUuid(getUuid())
            ).getCoins();
        } else {
            return player.getCoins();
        }
    }


    /**
     * @deprecated Used for local storage. Use {@link #setCoins(Value)} instead
     */
    @Deprecated
    default void _setCoins(final int coins) {
        CorePlayer player = toCorePlayer();
        if (player == null) {
            (getUuid() == null ?
                    OfflineCorePlayer.fromName(getName()) :
                    OfflineCorePlayer.fromUuid(getUuid())
            )._setCoins(coins);
        } else {
            player._setCoins(coins);
        }
    }


    /**
     * Calls a new {@link CoinValueModificationEvent} event.
     *
     * After it is processed, if the {@code event} has not been
     * cancelled, {@link #_setCoins(int)} is called with the
     * new coin value calculated (via {@link Value#appendTo(int)})
     * by this method and the database collection is updated with
     * the new value.
     *
     * By default the {@link #getCoinMultiplier()} is not applied.
     * If it needs to be, call {@code setCoins(value, true)}.
     *
     * @param   value
     *          The amount of coins being changed along with the
     *          change type, {@code GIVE}, {@code SET} or {@code TAKE}.
     * @throws  CoinValueModificationException If the amount provided ({@param value}) is a
     *          negative.
     * @throws  CoinValueModificationException If subtracting the provided amount from the
     *          current coin amount will result in a
     *          negative.
     * @see     CoinValueModificationEvent
     * @see     Value
     * @see     #setCoins(Value, boolean)
     * @see     #getCoinMultiplier()
     */
    default void setCoins(final Value<Integer> value) throws CoinValueModificationException {
        setCoins(value, false);
    }


    /**
     * Calls a new {@link CoinValueModificationEvent} event.
     *
     * After it is processed, if the {@code event} has not been
     * cancelled, {@link #_setCoins(int)} is called with the
     * new coin value calculated (via {@link Value#appendTo(int)})
     * by this method and the database collection is updated with
     * the new value.
     *
     * @param   value
     *          The amount of coins being changed along with the
     *          change type, {@code GIVE}, {@code SET} or {@code TAKE}.
     * @param   applyMultiplier
     *          If enabled and the value type is {@code GIVE},
     *          {@link #getCoinMultiplier()} is applied.
     * @throws  CoinValueModificationException
     *          If the amount provided ({@param value}) is a
     *          negative.
     * @throws  CoinValueModificationException
     *          If subtracting the provided amount from the
     *          current coin amount will result in a
     *          negative.
     * @see     CoinValueModificationEvent
     * @see     Value
     * @see     #getCoinMultiplier()
     */
    default void setCoins(Value<Integer> value,
                         final boolean applyMultiplier) throws CoinValueModificationException {
        // if possible, apply coin multiplier
        if (applyMultiplier && value.getType().equals(ValueType.GIVE)) {
            value = new Value<>(
                    MathUtil.floor((double) value.getValue() * getCoinMultiplier()),
                    ValueType.GIVE
            );
        }

        CoinValueModificationEvent event = new CoinValueModificationEvent(this, getCoins(), value);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            int newCoins = event.getNewCoins();

            _setCoins(newCoins);

            update(DataInfo.COINS, newCoins);
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
    default double getCoinMultiplier() {
        CorePlayer player = toCorePlayer();
        return player == null ? 1D : player.getCoinMultiplier();
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
     * @param factor The factor by which the base coin is multiplied.
     */
    default void setCoinMultiplier(final double factor) {
        CorePlayer player = toCorePlayer();
        if (player != null) player.setCoinMultiplier(factor);
    }
}