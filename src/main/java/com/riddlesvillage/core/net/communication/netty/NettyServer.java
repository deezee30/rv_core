/*
 * rv_core
 * 
 * Created on 14 July 2017 at 12:40 AM.
 */

package com.riddlesvillage.core.net.communication.netty;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.net.communication.CoreServer;
import com.riddlesvillage.core.net.communication.command.CommandRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.Validate;

import java.io.Closeable;
import java.io.IOException;

public class NettyServer implements Closeable {

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;
    private final CoreServer server;
    private final ChannelGroup channels;
    private final CommandRegistry cmdReg;
    private final SslContext sslCtx;

    public NettyServer(final CoreServer server,
                       final CommandRegistry cmdReg,
                       final SslContext sslCtx) {
        this(server, cmdReg, sslCtx, new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
    }

    public NettyServer(final CoreServer server,
                       final CommandRegistry cmdReg,
                       final SslContext sslCtx,
                       final ChannelGroup channels) {
        this.server = Validate.notNull(server);
        this.channels = Validate.notNull(channels);
        this.cmdReg = Validate.notNull(cmdReg);
        this.sslCtx = Validate.notNull(sslCtx);
    }

    public void run() throws Exception {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer(sslCtx, channels, cmdReg));

            channel = bootstrap.bind(server.getPort()).sync().channel();
        } finally {
            close();
        }
    }

    @Override
    public void close() throws IOException {
        // Wait until the server socket is closed.
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }

        bossGroup.shutdownGracefully();
        Future<?> f = workerGroup.shutdownGracefully();

        f.addListener((GenericFutureListener) future ->
                Core.log("Closed netty server `%s` on port '%s'", server.getPort()));
    }
}