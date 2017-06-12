/*
 * rv_core
 * 
 * Created on 11 June 2017 at 9:29 PM.
 */

package com.riddlesvillage.core.chat;

import com.riddlesvillage.core.player.CorePlayer;
import com.riddlesvillage.core.util.StringUtil;

import java.util.Optional;

class AdvertisementFilter implements ChatBlockFilter {

	@Override
	public boolean block(CorePlayer player, String message) {
		return !player.isHelper() && (StringUtil.containsAddress(message) || StringUtil.containsInetAddress(message));

	}

	@Override
	public Optional<String> getReason() {
		return Optional.of("chat.mute.no-ads");
	}
}