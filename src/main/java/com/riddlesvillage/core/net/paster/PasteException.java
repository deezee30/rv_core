package com.riddlesvillage.core.net.paster;

import com.riddlesvillage.core.CoreException;

public class PasteException extends CoreException {

    private static final long serialVersionUID = 8188274555648338076L;

    public PasteException(String message) {
        super(message);
    }

    public PasteException(String message,
                          Throwable cause) {
        super(message, cause);
    }

    public PasteException(Throwable cause) {
        super(cause);
    }

    public PasteException(String message,
                          Throwable cause,
                          boolean enableSuppression,
                          boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}