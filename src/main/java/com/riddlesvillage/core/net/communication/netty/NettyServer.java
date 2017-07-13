/*
 * rv_core
 * 
 * Created on 10 July 2017 at 11:15 PM.
 */

package com.riddlesvillage.core.net.communication.netty;

import com.riddlesvillage.core.Core;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.Closeable;
import java.io.IOException;

public class NettyServer implements Closeable {

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final ChannelFuture cf;

    private final int port;

    public NettyServer(int port) throws Exception {
        this.port = port;

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyChannelInboundAdapter());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        // Bind and start to accept incoming connections.
        cf = b.bind(port).sync();

        Core.log("Opened netty server on port %s", port);
    }

    public int getPort() {
        return port;
    }

    @Override
    public void close() throws IOException {
        // Wait until the server socket is closed.
        try {
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }

        bossGroup.shutdownGracefully();
        Future<?> f = workerGroup.shutdownGracefully();

        f.addListener((GenericFutureListener) future ->
                Core.log("Closed netty server on port %s", port));
    }
}