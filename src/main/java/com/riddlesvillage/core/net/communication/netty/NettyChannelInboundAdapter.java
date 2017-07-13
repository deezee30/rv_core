/*
 * rv_core
 * 
 * Created on 10 July 2017 at 11:10 PM.
 */

package com.riddlesvillage.core.net.communication.netty;

import com.riddlesvillage.core.Core;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class NettyChannelInboundAdapter extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(final ChannelHandlerContext ctx,
                            final Object message) throws Exception {
        ByteBuf in = (ByteBuf) message;
        // output
        Core.debug("Received command: %s", in.toString(CharsetUtil.US_ASCII));
        // process data here:

        // send the data back:
        ctx.writeAndFlush(in);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
                                final Throwable throwable) throws Exception {
        // send report back
        // Close the connection when an exception is raised.
        throwable.printStackTrace();
        ctx.close();
    }
}