/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.event;

import com.riddlesvillage.core.CoreException;

public class TokenValueModificationException extends CoreException {

    public TokenValueModificationException(String message,
                                           Object... components) {
        super(message, components);
    }

    public TokenValueModificationException(String message,
                                           Throwable cause) {
        super(message, cause);
    }

    public TokenValueModificationException(Throwable cause) {
        super(cause);
    }

    public TokenValueModificationException(String message,
                                           Throwable cause,
                                           boolean enableSuppression,
                                           boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}