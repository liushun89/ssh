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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufIndexFinder;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Handler of secure shell protocol greetings phase.
 *
 * @author Matthias Berndt
 */
@Sharable
final class VersionExchangeReader extends ByteToMessageDecoder {

    /**
     * A byte buffer index finder.
     */
    private ByteBufIndexFinder finder;

    /**
     * Simple empty constructor.
     */
    VersionExchangeReader() {
        super();

        this.finder = new CrLfByteBufIndexFinder();
    }

    @Override
    public VersionExchangeMessage decode(final ChannelHandlerContext context,
            final ByteBuf inboundBuffer) {
        final int crLfIdx = inboundBuffer.bytesBefore(this.finder);

        VersionExchangeMessage message = null;

        if (crLfIdx != -1 && crLfIdx != 0) {
            final ByteBuf buffer = inboundBuffer.readSlice(crLfIdx + 2);

            message =  new VersionExchangeMessageImpl(buffer);
        }
        return message;
    }
}
