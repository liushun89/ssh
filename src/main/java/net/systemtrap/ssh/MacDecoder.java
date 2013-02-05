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

import static net.systemtrap.ssh.ChannelHandlerUtil.generateName;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import io.netty.handler.codec.ByteToByteDecoder;

import java.util.concurrent.atomic.AtomicInteger;

final class MacDecoder extends ByteToByteDecoder {

    private static final Logger LOG = LogManager.getLogger(MacDecoder.class);

    private transient ChannelHandlerContext context;

    private transient final AtomicInteger pending =
            new AtomicInteger(0xFFFFFFFF);

    @Override
    public void afterAdd(
            final ChannelHandlerContext ctx) {
        LOG.entry();

        context = ctx;
        pending.compareAndSet(0xFFFFFFFF, 0);

        switchHandlerIfFeasible();

        LOG.exit();
    }

    private void decodeMac(
            final ByteBuf inbound,
            final ByteBuf outbound) {

        if (mayDecodeMac()) {
            doDecodeMac(inbound, outbound);
        }
    }

    private void doDecodeMac(
            final ByteBuf inbound,
            final ByteBuf outbound) {

        final int bytesReadable = inbound.readableBytes();
        final int pendingBytes = pending.get();
        final int chunkLength = min(pendingBytes, bytesReadable);

        outbound.ensureWritable(chunkLength, true);
        outbound.writeBytes(inbound, chunkLength);

        pending.compareAndSet(pendingBytes, pendingBytes - chunkLength);
    }

    private boolean mayDecodeMac() {
        return pending.get() > 0;
    }

    private boolean switchHandler() {
        return pending.get() == 0;
    }

    private void switchHandlerIfFeasible() {
        if (switchHandler()) {
            final ChannelHandler mac = new CipherDecoder();
            final String oldName = context.name();
            final String newName = generateName(mac);
            context.pipeline().replaceAndForward(oldName, newName, mac);
        }
    }

    @Override
    protected void decode(
            final ChannelHandlerContext ctx,
            final ByteBuf inbound,
            final ByteBuf outbound) {
        LOG.entry();

        decodeMac(inbound, outbound);

        switchHandlerIfFeasible();

        LOG.exit();
    }
}
