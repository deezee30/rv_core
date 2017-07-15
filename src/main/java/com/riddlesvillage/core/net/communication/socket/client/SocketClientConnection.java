/*
 * rv_core
 * 
 * Created on 12 July 2017 at 3:36 PM.
 */

package com.riddlesvillage.core.net.communication.socket.client;

import com.google.gson.reflect.TypeToken;
import com.riddlesvillage.core.net.communication.SocketApp;
import com.riddlesvillage.core.net.communication.socket.Security;
import com.riddlesvillage.core.net.communication.socket.SocketIO;
import com.riddlesvillage.core.net.communication.socket.SocketRunnable;
import com.riddlesvillage.core.net.communication.socket.SocketServerMessenger;
import com.riddlesvillage.core.security.AES;
import com.riddlesvillage.core.security.RSA;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

public class SocketClientConnection implements SocketRunnable {

    private final SocketClient client;
    private final Security security;
    private boolean enabled = false;
    private boolean handshaked = false;

    public SocketClientConnection(final SocketClientApp app,
                                  final String host,
                                  final int port,
                                  final Security.Level security) {
        client = new SocketClient(host, port, app);
        this.security = new Security(security);
        enabled = true;
    }

    @Override
    public void run() {
        int lvl = security.getLevel();
        loop: while (enabled) {
            // Attempt establish connection
            try {
                client.tryEstablishNewSocket();
            } catch (IOException e) {
                e.printStackTrace();
                // retry
                continue;
            }

            security.reset(); // Reset security data
            SocketIO io; // Open socket stream

            try {
                io = new SocketIO(client.getApp(), client.getSocket().get(), security);
            } catch (IOException e) {
                e.printStackTrace();
                // retry
                continue;
            }

            handshaked = false; // Default not handshaked

            String RSA_key = "";
            String AES_key = "";
            String message = "";
            while (enabled && client.isOpen()) {
                String read;

                try {
                    read = io.getReader().readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    // restart
                    // TODO: Do something to prevent data loss
                    continue loop;
                }

                // If end of stream, close it
                if (read == null) {
                    tryClose();
                } else {
                    // This isn't the end of stream, continue
                    // Is rsa encryption enabled? Do we have received the rsa key?
                    if (lvl >= 2 && security.getTarget().getRsa() == null) {

                        if (!read.equals("--end--"))
                            RSA_key += read; // The message is not fully received, continue

                        else {
                            try {
                                // Yay, we received the full message, convert it to PublicKey object
                                security.getTarget().setRsa(RSA.loadPublicKey(RSA_key)); // Done

                                // Now we need to send our rsa key
                                io.getWriter().println(RSA.savePublicKey(security.getSelf().getRsa().getPublic()));
                                io.getWriter().println("--end--");
                                io.getWriter().flush();
                            } catch (GeneralSecurityException | IOException e) {
                                e.printStackTrace();
                            }
                        }

                    } else if (lvl >= 1 && security.getTarget().getAes() == null) {

                        if (!read.equals("--end--")) AES_key += read;

                        else {
                            if (lvl == 1) {
                                log("target AES: " + AES_key);
                                security.getTarget().setAes(AES.toKey(AES_key));
                                String key = AES.toString(security.getSelf().getAes());
                                log("self AES: " + key);
                                io.getWriter().println(key);
                                io.getWriter().println("--end--");
                                io.getWriter().flush();
                            }
                            if (lvl == 2) {
                                security.getTarget().setAes(AES.toKey(RSA.decrypt(AES_key, security.getSelf().getRsa().getPrivate())));
                                io.getWriter().println(RSA.encrypt(AES.toString(security.getSelf().getAes()), security.getTarget().getRsa()));
                                io.getWriter().println("--end--");
                                io.getWriter().flush();
                            }
                        }

                    } else {
                        // We have received the rsa key
                        String decrypted = "";
                        if (lvl == 0) decrypted = read;
                        if (lvl >= 1) decrypted = AES.decrypt(read, security.getSelf().getAes());
                        log("<- " + read);
                        log("<- (%s)", decrypted);
                        if (decrypted != null && !decrypted.isEmpty()) {
                            if (!decrypted.equals("--end--")) message += decrypted;
                            else {
                                if (message != null && !message.isEmpty()) {
                                    Map<String, String> map = SocketServerMessenger.fromJson(message, new TypeToken<Map<String, String>>() {}.getType());
                                    if (map.get("channel").equals("SocketAPI")) {
                                        if (map.get("data").equals("handshake")) {
                                            io.writeJSON("SocketAPI", client.getApp().getName(), "handshake");
                                        } else if (map.get("data").equals("handshaked")) {
                                            handshaked = true;
                                            client.getApp().onHandshake(client);
                                        }
                                    } else client.getApp().onJson(client, map);
                                }
                                message = "";
                            }
                        }
                    }
                }
            }
        }

        log("Stopped client connection");
    }

    public boolean isHandshaked() {
        return handshaked;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean tryClose() {
        try {
            return close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean close() throws IOException {
        enabled = false;
        return client.close();
    }

    @Override
    public SocketApp getApp() {
        return client.getApp();
    }
}