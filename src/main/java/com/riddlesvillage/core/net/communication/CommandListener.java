/*
 * rv_core
 * 
 * Created on 10 July 2017 at 9:37 PM.
 */

package com.riddlesvillage.core.net.communication;

@FunctionalInterface
public interface CommandListener {

    boolean processCommand(final CoreServer from);
}