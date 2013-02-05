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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

import io.netty.channel.socket.SocketChannel;

/**
 * A socket channel initializer for the secure shell protocol.
 *
 * @author Matthias Berndt
 */
final class SshChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * Prevent package external construction.
     */
    SshChannelInitializer() {
        super();
    }

    @Override
    public void initChannel(final SocketChannel socketChannel) {
        setupPipeline(socketChannel.pipeline());
    }

    /**
     * Setup the initial, secure shell protocol specific handler pipeline.
     *
     * @param pipeline
     *   a channel pipeline to be configured
     */
    private void setupPipeline(final ChannelPipeline pipeline) {
        pipeline.addLast(new VersionExchangeWriter());
        pipeline.addLast(new VersionExchangeReader());
        pipeline.addLast(new VersionExchangeHandler());
    }
}
