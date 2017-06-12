/*
 * rv_core
 * 
 * Created on 12 June 2017 at 5:54 PM.
 */

package com.riddlesvillage.core.player.manager;

import com.riddlesvillage.core.chat.ChatViolation;
import com.riddlesvillage.core.player.CorePlayer;

public final class ViolationManager {

	private final CorePlayer player;
	private final ChatViolation chatViolation;

	public ViolationManager(CorePlayer player) {
		this.player = player;
		chatViolation = new ChatViolation(player);
	}

	public CorePlayer getPlayer() {
		return player;
	}

	public ChatViolation getChatViolation() {
		return chatViolation;
	}

	public void destroy() {
		chatViolation.cancel();
	}
}