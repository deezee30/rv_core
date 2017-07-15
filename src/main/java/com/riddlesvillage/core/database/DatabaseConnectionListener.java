/*
 * rv_core
 * 
 * Created on 15 July 2017 at 8:46 PM.
 */

package com.riddlesvillage.core.database;

import com.mongodb.event.ServerClosedEvent;
import com.mongodb.event.ServerDescriptionChangedEvent;
import com.mongodb.event.ServerListener;
import com.mongodb.event.ServerOpeningEvent;
import com.riddlesvillage.core.Logger;
import com.riddlesvillage.core.service.timer.Timer;
import org.apache.commons.lang3.Validate;

import java.util.concurrent.TimeUnit;

public final class DatabaseConnectionListener implements ServerListener {

    private Logger logger;
    private Timer timer;

    public DatabaseConnectionListener(final Logger logger) {
        this.logger = Validate.notNull(logger);
        timer = new Timer().onFinishExecute(() -> logger.log(
                "Successfully established a connection with Mongo database server in &e%sms",
                timer.getTime(TimeUnit.MILLISECONDS)
        )).start();
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public void serverOpening(ServerOpeningEvent event) {
        Database.get().setConnected(true);
        timer.forceStop();
    }

    @Override
    public void serverClosed(ServerClosedEvent event) {
        logger.log("Closed Mongo connection");
    }

    @Override
    public void serverDescriptionChanged(ServerDescriptionChangedEvent event) {}
}