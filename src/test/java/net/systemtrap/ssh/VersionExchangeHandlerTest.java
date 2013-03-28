/*
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.systemtrap.ssh;

import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

import org.testng.annotations.Test;

public class VersionExchangeHandlerTest {

    /**
     * Test case.
     */
    @Test
    public void simple0() {
        final VersionExchangeHandler handler = new VersionExchangeHandler();

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Channel chn = mock(Channel.class);
        VersionExchangeMessage msg = mock(VersionExchangeMessage.class);

        when(msg.isValid()).thenReturn(false);
        when(ctx.channel()).thenReturn(chn);

        handler.messageReceived(ctx, msg);

        verify(msg).isValid();
        verify(chn).close();
    }

    /**
     * Test case.
     */
    @Test
    public void simple1() {
        final VersionExchangeHandler handler = new VersionExchangeHandler();

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        ChannelPipeline ppl = mock(ChannelPipeline.class);
        VersionExchangeMessage msg = mock(VersionExchangeMessage.class);

        when(msg.isValid()).thenReturn(true);
        when(ctx.pipeline()).thenReturn(ppl);

        handler.messageReceived(ctx, msg);

        verify(msg).isValid();
        verify(ppl).addLast(isA(BinaryPacketSplitter.class));
        verify(ppl).removeAndForward(VersionExchangeWriter.class);
        verify(ppl).removeAndForward(VersionExchangeReader.class);
        verify(ppl).removeAndForward(VersionExchangeHandler.class);
    }
}
