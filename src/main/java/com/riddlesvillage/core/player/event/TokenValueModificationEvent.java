/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.event;

import com.riddlesvillage.core.database.value.Value;
import com.riddlesvillage.core.database.value.ValueType;
import com.riddlesvillage.core.player.statistic.TokensHolder;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.Cancellable;

/**
 * Called prior to modifying the player's token amount.
 *
 * <p>Cancelling the event will not modify the tokens.</p>
 *
 * <p>If the player's new token amount is less than 0 or
 * the amount provided is a negative, an exception is thrown.</p>
 */
public class TokenValueModificationEvent extends CoreProfileEvent implements Cancellable {

	private final	int			oldTokens;
	private final	int			changed;
	private final	ValueType	type;
	private			boolean		cancelled = false;

	/**
	 * Constructs a new {@code TokenValueModificationEvent}.
	 *
	 * <p>In case the amount provided or the supposedly new
	 * token amount is not applicable, an exception is thrown.</p>
	 *
	 * @param	player
	 * 			The user that can hold a token amount.
	 * @param	oldTokens
	 * 			The current amount of tokens the user has.
	 * @param	value
	 * 			The amount of tokens being changed along
	 *			with the change type, {@code GIVE}, {@code SET}
	 *			or {@code TAKE}.
	 * @throws	TokenValueModificationException
	 * 			If the amount provided ({@param value}) is a
	 * 			negative.
	 * @throws	TokenValueModificationException
	 * 			If subtracting the provided amount from the
	 * 			current token amount will result in a negative.
	 * @see     TokensHolder
	 * @see		Value
	 * @see		ValueType
	 */
	public TokenValueModificationEvent(TokensHolder player,
									   int oldTokens,
									   Value value) throws TokenValueModificationException {
		super(player);

		value = value.clone();

		Validate.notNull(oldTokens);
		Validate.notNull(value);
		Validate.isTrue(value.isInteger());

		this.oldTokens = oldTokens;
		this.type = value.getType();
		int changed = Integer.parseInt(value.toString());

		// Make sure tokens is 0 or bigger
		if (changed <= 0) {
			cancelled = true;
			throw new TokenValueModificationException("Cannot add tokens: The amount provided is a negative");
		}

		// If tokens are being taken away, make sure the new result is not 0
		if (type.equals(ValueType.TAKE) && oldTokens - changed < 0) {
			cancelled = true;
			throw new TokenValueModificationException("Subtracting %s tokens from %s will result in a negative balance", changed, player.getName());
		}

		this.changed = changed;
	}

	/**
	 * @return The user that can hold the tokens.
	 */
	public TokensHolder getPlayer() {
		return (TokensHolder) getProfile();
	}

	/**
	 * @return The amount of tokens the user had before the change.
	 */
	public int getOldTokens() {
		return oldTokens;
	}

	/**
	 * @return The new amount of tokens the user has after the change.
	 */
	public int getNewTokens() {
		return new Value<>(oldTokens, type).appendTo(changed);
	}

	/**
	 * @return The difference between the user's old and new token count.
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
