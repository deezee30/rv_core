/*
 * rv_core
 * 
 * Created on 11 June 2017 at 9:29 PM.
 */

package com.riddlesvillage.core.chat.filter;

import com.riddlesvillage.core.player.CorePlayer;

import java.util.Optional;

class MuteFilter implements ChatBlockFilter {

    @Override
    public boolean block(CorePlayer player, String message) {
        return player.isMuted();
    }

    @Override
    public Optional<String> getReason() {
        return Optional.of("chat.mute.reminder");
    }

    @Override
    public boolean violate() {
        return false;
    }
}