/*
 * rv_core
 * 
 * Created on 22 June 2017 at 9:55 PM.
 */

package com.riddlesvillage.core.player.event;

import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.player.manager.CorePlayerManager;
import org.bukkit.event.Cancellable;

public class CorePlayerEvent extends CoreProfileEvent implements Cancellable {

    protected static CorePlayerManager MANAGER = CorePlayerManager.getInstance();

    private boolean cancelled = false;

    protected CorePlayerEvent(CorePlayer profile) {
        super(profile);
    }

    @Override
    public CorePlayer getProfile() {
        return (CorePlayer) super.getProfile();
    }

    public CorePlayer getPlayer() {
        return getProfile();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}