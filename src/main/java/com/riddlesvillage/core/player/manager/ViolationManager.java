/*
 * rv_core
 * 
 * Created on 12 June 2017 at 5:54 PM.
 */

package com.riddlesvillage.core.player.manager;

import com.riddlesvillage.core.chat.ChatViolation;
import com.riddlesvillage.core.player.CorePlayer;
import org.apache.commons.lang3.Validate;

public final class ViolationManager {

    private final CorePlayer player;
    private final ChatViolation chatViolation;

    public ViolationManager(final CorePlayer player) {
        this.player = Validate.notNull(player);
        chatViolation = new ChatViolation(player);
    }

    public void destroy() {
        chatViolation.cancel();
    }

    public CorePlayer getPlayer() {
        return player;
    }

    public ChatViolation getChatViolation() {
        return chatViolation;
    }
}