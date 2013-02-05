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

import static java.lang.Math.max;
import static java.lang.Math.min;

import static net.systemtrap.ssh.ChannelHandlerUtil.generateName;

import static org.apache.logging.log4j.LogManager.getLogger;

import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import io.netty.handler.codec.ByteToByteDecoder;

import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.Cipher;
import javax.crypto.NullCipher;
import javax.crypto.ShortBufferException;

import java.nio.ByteBuffer;

final class CipherDecoder extends ByteToByteDecoder {

    private static final Logger LOG = getLogger(CipherDecoder.class);

    private transient ChannelHandlerContext context;

    private transient final AtomicInteger pending =
            new AtomicInteger(0xFFFFFFFF);


    @Override
    public void afterAdd(
            final ChannelHandlerContext ctx) {
        LOG.entry();

        // store the context the handler is bound to
        context = ctx;

        LOG.exit();
    }

    private Cipher cipher() {
        return new NullCipher();
    }

    private int decode(
            final Cipher cipher,
            final ByteBuf inbound,
            final ByteBuf outbound,
            final int pending) {

        final int bytesReadable = inbound.readableBytes();
        final int cipherBlockLength = max(8, cipher.getBlockSize());
        final int blockReadable = bytesReadable - bytesReadable % cipherBlockLength;
        final int blockLength = min(blockReadable, pending);

        final int iri = inbound.readerIndex();
        final int owi = outbound.writerIndex();

        outbound.ensureWritable(blockLength, true);

        final ByteBuffer inBlock = inbound.nioBuffer(iri, blockLength);
        final ByteBuffer outBlock = outbound.nioBuffer(owi, blockLength);

        try {
            cipher.update(inBlock, outBlock);
        } catch (ShortBufferException sbe) {
            throw new CipherDecodingException("SNO", sbe);
        }

        inbound.readerIndex(iri + blockLength);
        outbound.writerIndex(owi + blockLength);

        return blockLength;
    }

    private void decodeData(
            final ByteBuf inbound,
            final ByteBuf outbound) {

        final int pendingBytes = pending.get();
        final int delta = decode(cipher(), inbound, outbound, pendingBytes);
        pending.compareAndSet(pendingBytes, pendingBytes - delta);
    }

    private void decodeLength(
            final ByteBuf inbound,
            final ByteBuf outbound) {

        if (mayDecodeLength()) {
            doDecodeLength(inbound, outbound);
        }
    }

    private void doDecodeLength(
            final ByteBuf inbound,
            final ByteBuf outbound) {

        final int blockLength = decode(cipher(), inbound, outbound, 8);
        final int packetLength = outbound.getInt(outbound.readerIndex());
        final int remainingLength = packetLength - blockLength + 4;

        pending.compareAndSet(0xFFFFFFFF, remainingLength);
    }

    private boolean mayDecodeLength() {
        return pending.get() == 0xFFFFFFFF;
    }

    private boolean maySwitchHandler() {
        return pending.get() == 0;
    }

    private void switchHandler() {
        if (maySwitchHandler()) {
            ChannelHandler mac = new MacDecoder();
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

        decodeLength(inbound, outbound);
        decodeData(inbound, outbound);
        switchHandler();
    }
}
