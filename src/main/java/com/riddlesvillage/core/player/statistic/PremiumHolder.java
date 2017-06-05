/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.statistic;

import com.riddlesvillage.core.RiddlesCore;
import com.riddlesvillage.core.database.Database;
import com.riddlesvillage.core.database.DatabaseAPI;
import com.riddlesvillage.core.database.data.DataInfo;
import com.riddlesvillage.core.database.data.DataOperator;
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
public interface PremiumHolder extends CoreProfile {


	/**
	 * @return	{@code true} if the holder is a premium player,
	 * 			{@code false} if otherwise.
	 */
	boolean isPremium();


	/**
	 * Sets the local premium value in the holder's class instance.
	 *
	 * No processing needs to be done in this method.  Instead it
	 * is handled by {@link PremiumStatusModificationEvent} events.
	 *
	 * <b>This method should not be used by external classes</b>
	 *
	 * An example of how this method should look like is as follows:
	 * <code>
	 * private boolean premium = false;
	 *
	 * {@literal @}Override
	 * public void modifyPremium(boolean premium) {@literal {}
	 *     this.premium = premium;
	 * {@literal }}
	 * </code>
	 *
	 * @param	premium
	 * 			The new premium value.
	 * @see		#setPremium(boolean)
	 * @see		PremiumStatusModificationEvent
	 * @since	2.0
	 * @deprecated Used for local storage. Use {@link #setPremium(boolean)} instead
	 */
	void modifyPremium(boolean premium);


	/**
	 * Calls a new {@link PremiumStatusModificationEvent} event.
	 *
	 * After it is processed, if the {@code event} has not been
	 * cancelled, {@link #modifyPremium(boolean)} is called with
	 * the {@param premium} parameter and the database
	 * is updated with the new value.
	 *
	 * @param	premium
	 * 			The new premium value.
	 * @see		PremiumStatusModificationEvent
	 * @since	2.0
	 */
	default void setPremium(boolean premium) {
		PremiumStatusModificationEvent event = new PremiumStatusModificationEvent(this, premium);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			modifyPremium(premium);

			DatabaseAPI.update(
					Database.getMainCollection(),
					getUuid(),
					DataOperator.$SET,
					DataInfo.PREMIUM,
					premium,
					updateResult -> RiddlesCore.logIf(
							!updateResult.wasAcknowledged(),
							"Failed updating %s's premium status to %s",
							getName(),
							premium
					)
			);
		}
	}
}