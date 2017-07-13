/*
 * rv_core
 * 
 * Created on 12 July 2017 at 3:40 PM.
 */

package com.riddlesvillage.core.net.communication.socket;

import com.riddlesvillage.core.net.communication.SocketApp;
import com.riddlesvillage.core.security.AES;
import org.apache.commons.lang3.Validate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public final class SocketIO {

    private final SocketApp app;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Security security;

    public SocketIO(SocketApp app, Socket socket, Security security) throws IOException {
        this.app = Validate.notNull(app);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream());
        this.security = security;
    }

    public void writeJSON(String channel, String name, String data) {
        HashMap<String, String> hashmap = new HashMap<>();
        hashmap.put("channel", channel);
        hashmap.put("name", name);
        hashmap.put("data", data);
        String json = SocketServerMessenger.toJson(hashmap);
        write(json);
    }

    private void write(String data) {
        String[] split = SocketIO.split(data, 20);
        if (security.getLevel() == 0) {
            for (String str : split) writer.println(str);
            writer.println("--end--");
        }
        if (security.getLevel() >= 1) {
            for (String str : split) {
                String aes = AES.encrypt(str, security.getTarget().getAes());
                app.log("-> " + aes);
                writer.println(aes);
            }
            String aes = AES.encrypt("--end--", security.getTarget().getAes());
            app.log("-> " + aes);
            writer.println(aes);
        }
        writer.flush();
    }

    public SocketApp getApp() {
        return app;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public Security getSecurity() {
        return security;
    }

    public static String[] split(String input, int max) {
        return input.split("(?<=\\G.{" + max + "})");
    }
}