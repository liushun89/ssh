package net.systemtrap.ssh;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.testng.annotations.Test;

import io.netty.channel.ChannelHandlerContext;

public class VersionExchangeWriterTest {

    @Test
    public void ensureInboundBufferForwarding() {
        final VersionExchangeWriter handler = new VersionExchangeWriter();

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

        handler.inboundBufferUpdated(ctx);

        verify(ctx).fireInboundBufferUpdated();
    }
}
