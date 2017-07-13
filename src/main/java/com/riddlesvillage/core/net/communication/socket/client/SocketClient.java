/*
 * rv_core
 * 
 * Created on 12 July 2017 at 3:38 PM.
 */

package com.riddlesvillage.core.net.communication.socket.client;

import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class SocketClient {

    private final String host;
    private final int port;
    private final SocketClientApp app;
    private Optional<Socket> socket = Optional.empty();

    public SocketClient(final String host,
                        final int port,
                        final SocketClientApp app) {
        this.host = Validate.notNull(host);
        this.port = port;
        this.app = Validate.notNull(app);
    }

    public void tryEstablishNewSocket() throws IOException {
        Socket socket = new Socket(host, port);
        socket.setTcpNoDelay(true);
        this.socket = Optional.of(socket);
        app.onConnect(this);
    }

    public boolean close() throws IOException {
        if (isOpen()) {
            socket.get().close();
            app.onDisconnect(this);
            app.log("Successfully closed connection");
            return true;
        }

        return false;
    }

    public boolean isConnected() {
        return socket.isPresent() && socket.get().isConnected();
    }

    public boolean isOpen() {
        return isConnected() && !socket.get().isClosed();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Optional<Socket> getSocket() {
        return socket;
    }

    public SocketClientApp getApp() {
        return app;
    }
}