/*
 * rv_core
 * 
 * Created on 20 June 2017 at 8:48 PM.
 */

package com.riddlesvillage.core.world.region;

import com.riddlesvillage.core.CoreException;

public class RegionException extends CoreException {

	public RegionException(String message, Object... components) {
		super(message, components);
	}

	public RegionException(String message, Throwable cause) {
		super(message, cause);
	}

	public RegionException(Throwable cause) {
		super(cause);
	}

	public RegionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}