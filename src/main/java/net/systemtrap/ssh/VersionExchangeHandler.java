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

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.ChannelPipeline;

@Sharable
final class VersionExchangeHandler extends
        ChannelInboundMessageHandlerAdapter<VersionExchangeMessage> {

    /**
     * The VersionExchangeHandler.
     */
    static final ChannelHandler BINARY_CHUNK_SPLITTER =
            new BinaryPacketSplitter();

    @Override
    public void messageReceived(final ChannelHandlerContext ctx,
            final VersionExchangeMessage msg) {
        if (msg.isValid()) {
            rebuildPipeline(ctx.pipeline());
        } else {
            terminateChannel(ctx.channel());
        }
    }

    private void rebuildPipeline(final ChannelPipeline pipeline) {
        pipeline.addLast(new CipherDecoder());
        pipeline.addLast(new BinaryPacketSplitter());

        pipeline.removeAndForward(VersionExchangeWriter.class);
        pipeline.removeAndForward(VersionExchangeReader.class);
        pipeline.removeAndForward(VersionExchangeHandler.class);
    }

    private void terminateChannel(final Channel channel) {
        channel.close();
    }
}
