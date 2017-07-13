/*
 * rv_core
 * 
 * Created on 13 July 2017 at 2:56 PM.
 */

package com.riddlesvillage.core.net.communication;

import com.riddlesvillage.core.Logger;
import org.apache.commons.lang3.Validate;

public abstract class AbstractServerMessenger implements ServerMessenger {

    private final String name;
    private final Logger logger;

    protected AbstractServerMessenger(final String name) {
        this.name = Validate.notNull(name);
        logger = new Logger();
        logger.setPrefix("[" + name + " pipeline]");
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public final String getName() {
        return name;
    }
}