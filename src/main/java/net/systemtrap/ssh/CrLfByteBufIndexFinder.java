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

/**
 * Locates a CR LF sequence in a buffer.
 *
 * @author Matthias Berndt
 */
final class CrLfByteBufIndexFinder implements ByteBufIndexFinder {

    /**
     * Default constructor.
     */
    CrLfByteBufIndexFinder() {
        // intentionally empty
    }

    /**
     * Performs a test for CF LF at the given index position in the given
     * buffer.
     *
     * @see ByteBufIndexFinder#find(ByteBuf, int)
     *
     * @param buffer
     *   the buffer to search in
     * @param index
     *   the index position in the buffer to search at
     * @return
     *   true, if CR LF is found at the index position in the buffer
     */
    @Override
    public
    boolean find(final ByteBuf buffer, final int index) {
        boolean found;

        if (validateIndex(buffer, index)
                && sufficentBytesReadable(buffer, index)) {
            final byte byte0 = buffer.getByte(index);
            final byte byte1 = buffer.getByte(index + 1);
            found = byte0 == '\r' && byte1 == '\n';
        } else {
            found = false;
        }

        return found;
    }

    /**
     * Determite if sufficent bytes are readable at the index position.
     *
     * @param buffer
     *   the buffer to search in
     * @param index
     *   the index position in the buffer to search at
     * @return
     *   true, if sufficent bytes are readable at the index position
     */
    private boolean sufficentBytesReadable(final ByteBuf buffer,
            final int index) {
        return buffer.writerIndex() - index >= 2;
    }

    /**
     * Determite if the index is in a valid range.
     *
     * @param buffer
     *   the buffer to search in
     * @param index
     *   the index position in the buffer to search at
     * @return
     *   true, if the index is in a valid range
     */
    private boolean validateIndex(final ByteBuf buffer, final int index) {
        return index >= buffer.readerIndex() && index <= buffer.writerIndex();
    }
}
