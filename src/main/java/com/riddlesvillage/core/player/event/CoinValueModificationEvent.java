/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.event;

import com.riddlesvillage.core.database.value.Value;
import com.riddlesvillage.core.database.value.ValueType;
import com.riddlesvillage.core.player.statistic.CoinsHolder;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.Cancellable;

/**
 * Called prior to modifying the player's coin amount.
 *
 * <p>Cancelling the event will not modify the coins.</p>
 *
 * <p>If the player's new coin amount is less than 0 or
 * the amount provided is a negative, an exception is thrown.</p>
 */
public class CoinValueModificationEvent extends CoreProfileEvent implements Cancellable {

	private final CoinsHolder player;
	private final int oldCoins;
	private final int changed;
	private final ValueType type;
	private boolean cancelled = false;

	/**
	 * Constructs a new {@code CoinValueModificationEvent}.
	 *
	 * <p>In case the amount provided or the supposedly new
	 * coin amount is not applicable, an exception is thrown.</p>
	 *
	 * @param	player
	 * 			The user that can hold a coin amount.
	 * @param   oldCoins
	 * 			The current amount of coins the user has.
	 * @param   value
	 * 			The amount of coins being changed along
	 * 			with the change type, {@code GIVE}, {@code SET}
	 * 			or {@code TAKE}.
	 * @throws	CoinValueModificationException
	 * 			If the amount provided ({@param value}) is a
	 * 			negative.
	 * @throws	CoinValueModificationException
	 * 			If subtracting the provided amount from the
	 * 			current coin amount will result in a negative.
	 * @see     Value
	 * @see     ValueType
	 * @see		CoinsHolder
	 */
	public CoinValueModificationEvent(CoinsHolder player,
									  int oldCoins,
									  Value value) throws CoinValueModificationException {
		super(player);

		value = value.clone();

		Validate.notNull(oldCoins);
		Validate.notNull(value);
		Validate.isTrue(value.isInteger());

		this.player = player;
		this.oldCoins = oldCoins;
		this.type = value.getType();
		int changed = Integer.parseInt(value.toString());

		// Make sure coins is 0 or bigger
		if (changed <= 0) {
			cancelled = true;
			throw new CoinValueModificationException("Cannot add coins: The amount provided is a negative");
		}

		// If coins are being taken away, make sure the new result is not 0
		if (type.equals(ValueType.TAKE) && oldCoins - changed < 0) {
			cancelled = true;
			throw new CoinValueModificationException("Subtracting %s coins from %s will result in a negative balance", changed, player.getName());
		}

		this.changed = changed;
	}

	/**
	 * @return The user that can hold the coins.
	 */
	public CoinsHolder getPlayer() {
		return player;
	}

	/**
	 * @return The amount of coins the user had before the change.
	 */
	public int getOldCoins() {
		return oldCoins;
	}

	/**
	 * @return The new amount of coins the user has after the change.
	 */
	public int getNewCoins() {
		return new Value<>(oldCoins, type).appendTo(changed);
	}

	/**
	 * @return The difference between the user's old and new coin count.
	 */
	public int getAmountChanged() {
		return changed;
	}

	/**
	 * @return The type of change that was applied.
	 */
	public ValueType getChangedType() {
		return type;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
