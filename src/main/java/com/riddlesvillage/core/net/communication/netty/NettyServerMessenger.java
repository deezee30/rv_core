/*
 * rv_core
 * 
 * Created on 10 July 2017 at 9:40 PM.
 */

package com.riddlesvillage.core.net.communication.netty;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.net.communication.AbstractServerMessenger;
import com.riddlesvillage.core.net.communication.CoreServer;

public class NettyServerMessenger extends AbstractServerMessenger {

    private static final NettyServerMessenger instance = new NettyServerMessenger(Core.get().getName());

    protected NettyServerMessenger(final String name) {
        super(name);
    }

    @Override
    public String receive(final CoreServer from) {
        return null;
    }

    @Override
    public void send(final CoreServer to,
                     final String command) {

    }

    public static NettyServerMessenger setup() {
        return instance;
    }
}