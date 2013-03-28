package net.systemtrap.ssh;

import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.ChannelStateHandlerAdapter;

final class CloseHandler extends ChannelStateHandlerAdapter {

    @Override
    public void inboundBufferUpdated(final ChannelHandlerContext ctx)
            throws Exception {
        ctx.fireInboundBufferUpdated();
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
    }
}
