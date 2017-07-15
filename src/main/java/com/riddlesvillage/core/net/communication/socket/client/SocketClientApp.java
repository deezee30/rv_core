/*
 * rv_core
 *
 * Created on 12 July 2017 at 3:35 PM.
 */

package com.riddlesvillage.core.net.communication.socket.client;

import com.riddlesvillage.core.net.communication.SocketApp;

import java.util.Map;

public interface SocketClientApp extends SocketApp {

    void onConnect(final SocketClient client);

    void onDisconnect(final SocketClient client);

    void onHandshake(final SocketClient client);

    void onJson(final SocketClient client,
                final Map<String, String> map);
}