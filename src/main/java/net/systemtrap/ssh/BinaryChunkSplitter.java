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

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

import io.netty.handler.codec.ByteToMessageDecoder;

import com.google.common.base.Optional;

@Sharable
final class BinaryChunkSplitter extends ByteToMessageDecoder {

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final ByteBuf in) {
        final int readIdx = in.readerIndex();

        RawBinaryMessage result = null;

        if (in.readableBytes() >= 4) {
            final int rInt = in.getInt(readIdx);

            validateMessageLength(rInt);

            result = read(in).orNull();
        }

        return result;
    }

    private void validateMessageLength(final int in) {
        final long unsigned = in & 0xFFFFFFFFL;
        if (1 >= unsigned || unsigned >= 2 << 18) {
            // TODO throw packet size
        }
    }

    private static Optional<RawBinaryMessage> read(final ByteBuf in) {

        final int chunkLength = in.readInt() + macLength();

        Optional<RawBinaryMessage> result = Optional.absent();

        if (in.isReadable(chunkLength)) {
            final ByteBuf slice = in.readSlice(chunkLength);
            final RawBinaryMessageImpl rbm = new RawBinaryMessageImpl(slice);

            result = Optional.<RawBinaryMessage>of(rbm);
        }

        return result;
    }

    private static int macLength() {
        return 0;
    }
}
