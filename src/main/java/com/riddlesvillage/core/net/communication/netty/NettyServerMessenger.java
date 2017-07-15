/*
 * rv_core
 * 
 * Created on 10 July 2017 at 9:40 PM.
 */

package com.riddlesvillage.core.net.communication.netty;

import com.riddlesvillage.core.net.communication.AbstractServerMessenger;
import com.riddlesvillage.core.net.communication.CoreServer;
import com.riddlesvillage.core.net.communication.CoreServerRegistry;
import com.riddlesvillage.core.net.communication.ServerMessengerException;
import com.riddlesvillage.core.net.communication.command.CommandRegistry;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.commons.lang3.Validate;

import javax.net.ssl.SSLException;

public class NettyServerMessenger extends AbstractServerMessenger {

    private static NettyServerMessenger instance;

    private final SslContext sslCtx;
    private final CommandRegistry cmdReg;

    private NettyServerMessenger(final String name,
                                 final CoreServerRegistry svrReg,
                                 final CommandRegistry cmdReg) throws SSLException {
        super(name, svrReg);
        this.cmdReg = Validate.notNull(cmdReg);

        // Configure SSL
        sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
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

    public SslContext getSslContext() {
        return sslCtx;
    }


    public CommandRegistry getCommandRegistry() {
        return cmdReg;
    }

    public static NettyServerMessenger setup(final CoreServerRegistry svrReg,
                                             final CommandRegistry cmdReg)
            throws ServerMessengerException, SSLException {
        return setup("Netty", svrReg, cmdReg);
    }

    public static NettyServerMessenger setup(final String name,
                                             final CoreServerRegistry svrReg,
                                             final CommandRegistry cmdReg)
            throws ServerMessengerException, SSLException {
        if (instance != null)
            throw new ServerMessengerException("Netty server messanges has already been set up");

        return instance = new NettyServerMessenger(name, svrReg, cmdReg);
    }

    public static NettyServerMessenger getInstance() {
        return instance;
    }
}