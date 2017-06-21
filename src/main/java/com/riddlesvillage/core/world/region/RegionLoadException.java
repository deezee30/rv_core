/*
 * rv_core
 * 
 * Created on 19 June 2017 at 4:02 PM.
 */

package com.riddlesvillage.core.world.region;

import com.riddlesvillage.core.CoreException;

public class RegionLoadException extends CoreException {

	public RegionLoadException(String message, Object... components) {
		super(message, components);
	}

	public RegionLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public RegionLoadException(Throwable cause) {
		super(cause);
	}

	public RegionLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}