/*
 * rv_core
 * 
 * Created on 10 July 2017 at 9:39 PM.
 */

package com.riddlesvillage.core.net.communication;

import com.riddlesvillage.core.Logger;

public interface ServerMessenger {

    Logger getLogger();

    String receive(final CoreServer from,
                   final String command);

    void send(final CoreServer to,
              final String command);

    CoreServerRegistry getServerRegistry();
}