/*
 * rv_core
 *
 * Created on 12 July 2017 at 3:35 PM.
 */

package com.riddlesvillage.core.net.communication.socket.client;

import com.riddlesvillage.core.net.communication.SocketApp;

import java.util.Map;

public interface SocketClientApp extends SocketApp {

    void onConnect(SocketClient client);

    void onDisconnect(SocketClient client);

    void onHandshake(SocketClient client);

    void onJSON(SocketClient client, Map<String, String> map);
}