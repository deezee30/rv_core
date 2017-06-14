package com.riddlesvillage.core.chat.filter;

import com.riddlesvillage.core.player.CorePlayer;

import java.util.Optional;

/**
 * Created by Matthew E on 6/13/2017.
 */
public class SingleCharacterFilter implements ChatBlockFilter {
    @Override
    public boolean block(CorePlayer player, String message) {
        if (message.length() < 2) {
            return true;
        }
        return false;
    }

    @Override
    public Optional<String> getReason() {
        return Optional.of("chat.mute.no-single-character");
    }

    @Override
    public boolean violate() {
        return false;
    }
}
