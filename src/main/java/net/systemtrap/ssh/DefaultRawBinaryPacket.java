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

import static com.google.common.base.Preconditions.checkNotNull;
import static io.netty.buffer.Unpooled.unmodifiableBuffer;
import static org.apache.logging.log4j.LogManager.getLogger;

import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;

final class DefaultRawBinaryPacket implements RawBinaryPacket {

    private static final Logger LOG = getLogger(DefaultRawBinaryPacket.class);

    private final transient ByteBuf readOnlyChunk;

    DefaultRawBinaryPacket(final ByteBuf chunk) {
        checkNotNull(chunk);

        readOnlyChunk = unmodifiableBuffer(chunk.slice());

        chunk.retain();
    }

    @Override
    public ByteBuf raw() {
        return readOnlyChunk;
    }

    @Override
    public int payloadLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ByteBuf payload() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf padding() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf mac() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int rawLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int paddingLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int macLength() {
        // TODO Auto-generated method stub
        return 0;
    }
}
