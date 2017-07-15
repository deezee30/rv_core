/*
 * rv_core
 * 
 * Created on 15 July 2017 at 11:53 PM.
 */

package com.riddlesvillage.core.database;

import com.riddlesvillage.core.CoreException;

public class DatabaseException extends CoreException {

    public DatabaseException(final String message,
                             final Object... components) {
        super(message, components);
    }

    public DatabaseException(final String message,
                             final Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(final Throwable cause) {
        super(cause);
    }

    public DatabaseException(final String message,
                             final Throwable cause,
                             final boolean enableSuppression,
                             final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}