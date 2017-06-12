/*
 * rv_core
 * 
 * Created on 11 June 2017 at 8:22 PM.
 */

package com.riddlesvillage.core.chat;

import com.riddlesvillage.core.player.CorePlayer;

import java.util.Optional;

public interface ChatBlockFilter {

	boolean block(CorePlayer player, String message);

	Optional<String> getReason();
}