/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.event;

import com.riddlesvillage.core.player.statistic.PremiumHolder;
import org.bukkit.event.Cancellable;

/**
 * Called prior to modifying the player's premium status.
 *
 * <p>Cancelling the event will not modify the user's
 * premium status.</p>
 *
 * <p>If the user's current premium status equals to the
 * new one being applied, the event is cancelled and nothing
 * occurs further.</p>
 */
public class PremiumStatusModificationEvent extends CoreProfileEvent implements Cancellable {

    private final boolean premium;
    private boolean cancelled;

    /**
     * Constructs a new {@code PremiumStatusModificationEvent}.
     *
     * <p>If the user's current premium status equals to the
     * new one being applied, the event is cancelled and nothing
     * occurs further.  Similarly, if {@code {@link #isCancelled()}
     * != true}, then {@code {@link #isPremium()} != {@link
     * PremiumHolder#isPremium()}}.</p>
     *
     * @param	player
     * 			The user that can hold a premium status.
     * @param	premium
     * 			The new status.
     * @see		#isPremium()
     */
    public PremiumStatusModificationEvent(PremiumHolder player,
                                          boolean premium) {
        super(player);

        /*
         * Cancel the event if the state of the player's premium
         * status isn't changed.
         */
        if (this.premium = premium == player.isPremium()) {
            cancelled = true;
        }
    }

    @Override
    public PremiumHolder getProfile() {
        return (PremiumHolder) super.getProfile();
    }

    public boolean isPremium() {
        return premium;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}