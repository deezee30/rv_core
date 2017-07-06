/*
 * rv_core
 * 
 * Created on 06 July 2017 at 1:29 PM.
 */

package com.riddlesvillage.core.pgm.loader;

import com.riddlesvillage.core.CoreException;

public class MapLoaderException extends CoreException {

    public MapLoaderException(final String message,
                              final Object... components) {
        super(message, components);
    }

    public MapLoaderException(final String message,
                              final Throwable cause) {
        super(message, cause);
    }

    public MapLoaderException(final Throwable cause) {
        super(cause);
    }

    public MapLoaderException(final String message,
                              final Throwable cause,
                              final boolean enableSuppression,
                              final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}