/*
 * MaulssLib
 * 
 * Created on 11 February 2015 at 4:47 PM.
 */

package com.riddlesvillage.core.world.region;

import com.riddlesvillage.core.CoreException;
import com.sun.istack.internal.NotNull;

public class RegionBoundsException extends CoreException {

	private static final long serialVersionUID = -2633695118196820017L;

	public RegionBoundsException(@NotNull String message) {
		super(message);
	}

	public RegionBoundsException(@NotNull String message,
								 @NotNull Throwable cause) {
		super(message, cause);
	}

	public RegionBoundsException(@NotNull Throwable cause) {
		super(cause);
	}

	public RegionBoundsException(@NotNull String message,
								 @NotNull Throwable cause,
								 boolean enableSuppression,
								 boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}