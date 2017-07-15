/*
 * rv_core
 * 
 * Created on 13 July 2017 at 5:10 PM.
 */

package com.riddlesvillage.core.net.communication.socket.server;

import com.riddlesvillage.core.net.communication.SocketApp;

import java.util.Map;

public interface SocketServerApp extends SocketApp {

    void onConnect(final SocketServer server);

    void onHandshake(final SocketServer server,
                     final String name);

    void onJson(final SocketServer server,
                final Map<String, String> map);

    void onDisconnect(final SocketServer server);

    void run(final SocketServer server);
}