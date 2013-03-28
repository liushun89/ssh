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

import static java.lang.Math.min;

import io.netty.channel.ChannelHandler.Sharable;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelStateHandlerAdapter;

import java.nio.charset.Charset;

@Sharable
final class VersionExchangeWriter extends ChannelStateHandlerAdapter {

    private static final String MESSAGE = "SSH-2.0-net.systemtrap.ssh\r\n";

    private static final Charset CS_ASCII = Charset.forName("US-ASCII");

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        final ByteBuf msg = Unpooled.copiedBuffer(MESSAGE, CS_ASCII);
        pushMessage(ctx, msg);
    }

    private static void pushMessage(final ChannelHandlerContext ctx,
            final ByteBuf msg) {
        final ByteBuf out = ctx.nextOutboundByteBuffer();

        transferBytes(msg, out);
        ctx.flush().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future)
                    throws Exception {
                if (msg.isReadable())
                    pushMessage(ctx, msg);
            }
        });
    }

    private static void transferBytes(final ByteBuf src, final ByteBuf dst) {
        final int srcCap = src.readableBytes();
        final int maxCap = 1 << 8;

        final int prefered = min(srcCap, maxCap);
        final int length = checkedWritableLength(dst, prefered);

        dst.writeBytes(src, length);
    }

    private static int determineWritableLength(final ByteBuf buffer,
            final int preferedLength) {
        buffer.ensureWritable(preferedLength, true);
        return min(buffer.writableBytes(), preferedLength);
    }

    private static int checkedWritableLength(final ByteBuf buffer,
            final int preferedLength) {
        final int length = determineWritableLength(buffer, preferedLength);
        checkWritableLength(length);
        return length;
    }

    private static void checkWritableLength(final int length) {
        if (length == 0) {
            // TODO throw some exception 
        }
    }

    @Override
    public void inboundBufferUpdated(final ChannelHandlerContext ctx) {
        ctx.fireInboundBufferUpdated();
    }
}
