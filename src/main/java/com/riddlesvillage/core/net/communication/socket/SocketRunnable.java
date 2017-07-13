/*
 * rv_core
 * 
 * Created on 13 July 2017 at 3:27 PM.
 */

package com.riddlesvillage.core.net.communication.socket;

import com.riddlesvillage.core.net.communication.SocketApp;

public interface SocketRunnable extends Runnable {

    SocketApp getApp();

    default void log(String log, Object... replacements) {
        getApp().getLogger().log(log, replacements);
    }
}