package com.riddlesvillage.core.api.file.yaml;

/**
 * Created by matt1 on 3/22/2017.
 */
public class YamlLoadException extends Exception {

    public YamlLoadException() {
    }

    public YamlLoadException(String message) {
        super(message);
    }

    public YamlLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public YamlLoadException(Throwable cause) {
        super(cause);
    }

    public YamlLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
