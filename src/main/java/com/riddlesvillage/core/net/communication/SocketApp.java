/*
 * rv_core
 * 
 * Created on 13 July 2017 at 3:05 PM.
 */

package com.riddlesvillage.core.net.communication;

import com.riddlesvillage.core.Logger;

public interface SocketApp {

    String getName();

    Logger getLogger();

    default void log(String log, Object... replacements) {
        getLogger().log(log, replacements);
    }
}