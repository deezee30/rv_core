/*
 * rv_core
 * 
 * Created on 03 June 2017 at 7:23 PM.
 */

package com.riddlesvillage.core.player.event;

import com.riddlesvillage.core.CoreException;

public class CoinValueModificationException extends CoreException {

	public CoinValueModificationException(String message,
										   Object... components) {
		super(message, components);
	}

	public CoinValueModificationException(String message,
										   Throwable cause) {
		super(message, cause);
	}

	public CoinValueModificationException(Throwable cause) {
		super(cause);
	}

	public CoinValueModificationException(String message,
										   Throwable cause,
										   boolean enableSuppression,
										   boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}