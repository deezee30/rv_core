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
    private final CoreServerRegistry serverRegistry;

    protected AbstractServerMessenger(final CoreServerRegistry registry) {
        this("Core", registry);
    }

    protected AbstractServerMessenger(final String name,
                                      final CoreServerRegistry serverRegistry) {
        this.name = Validate.notNull(name);
        this.serverRegistry = Validate.notNull(serverRegistry);
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

    @Override
    public final CoreServerRegistry getServerRegistry() {
        return serverRegistry;
    }
}