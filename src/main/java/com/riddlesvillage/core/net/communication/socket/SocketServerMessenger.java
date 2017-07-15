/*
 * rv_core
 * 
 * Created on 12 July 2017 at 3:29 PM.
 */

package com.riddlesvillage.core.net.communication.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.riddlesvillage.core.net.communication.AbstractServerMessenger;
import com.riddlesvillage.core.net.communication.CoreServer;
import com.riddlesvillage.core.net.communication.CoreServerRegistry;

import java.lang.reflect.Type;

public class SocketServerMessenger extends AbstractServerMessenger {

    private static final Gson gson = new GsonBuilder().create();

    public SocketServerMessenger(final String name,
                                 final CoreServerRegistry registry) {
        super(name, registry);
    }

    @Override
    public String receive(final CoreServer from,
                          final String command) {
        return null;
    }

    @Override
    public void send(final CoreServer to,
                     final String command) {

    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> token) {
        return gson.fromJson(json, token);
    }

    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }
}