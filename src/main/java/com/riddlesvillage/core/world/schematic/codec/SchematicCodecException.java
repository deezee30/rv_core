/*
 * rv_core
 * 
 * Created on 17 July 2017 at 12:07 AM.
 */

package com.riddlesvillage.core.world.schematic.codec;

import com.riddlesvillage.core.Logger;

import java.io.IOException;

public class SchematicCodecException extends IOException {

    public SchematicCodecException(final String message,
                                   final Object... components) {
        super(Logger.buildMessage(message, components));
    }

    public SchematicCodecException(final String message,
                                   final Throwable cause) {
        super(message, cause);
    }

    public SchematicCodecException(final Throwable cause) {
        super(cause);
    }
}