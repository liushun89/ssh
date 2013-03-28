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
import io.netty.buffer.Unpooled;

import junit.framework.Assert;

import org.testng.annotations.Test;

/**
 * Test.
 *
 * @author Matthias Berndt
 */
public final class CrLfByteBufIndexFinderTest {

    /**
     * Test case.
     */
    @Test
    public void findInEmptyBuffer() {
        final ByteBufIndexFinder finder = new CrLfByteBufIndexFinder();
        final ByteBuf buffer = Unpooled.copiedBuffer(new byte[] {});

        Assert.assertFalse(finder.find(buffer, 0));
    }

    /**
     * Test case.
     */
    @Test
    public void findInTooSmallBuffer() {
        final ByteBufIndexFinder finder = new CrLfByteBufIndexFinder();
        final ByteBuf buffer = Unpooled.copiedBuffer(new byte[] {0x00});

        Assert.assertFalse(finder.find(buffer, 0));
    }

    /* i
     * |
     * v0    1
     * +----+----+
     * |0x0D|0x0A|
     * +----+----+
     * ^         ^
     * |         |
     * r         w
     */
    /**
     * Test case.
     */
    @Test
    public void findAtReadPosition() {
        final ByteBufIndexFinder finder = new CrLfByteBufIndexFinder();
        final ByteBuf buffer = Unpooled.copiedBuffer(new byte[] {0x0D, 0x0A});

        Assert.assertTrue(finder.find(buffer, 0));
    }

    /* i
     * |
     * v0    1
     * +----+----+
     * |0x0D|0x0B|
     * +----+----+
     * ^         ^
     * |         |
     * r         w
     */
    /**
     * Test case.
     */
    @Test
    public void findAtReadPositionFirstByteMatch() {
        final ByteBufIndexFinder finder = new CrLfByteBufIndexFinder();
        final ByteBuf buffer = Unpooled.copiedBuffer(new byte[] {0x0D, 0x0B});

        Assert.assertFalse(finder.find(buffer, 0));
    }


    /* i
     * |
     * v0    1
     * +----+----+
     * |0x0C|0x0A|
     * +----+----+
     * ^         ^
     * |         |
     * r         w
     */
    /**
     * Test case.
     */
    @Test
    public void findAtReadPositionSecondByteMatch() {
        final ByteBufIndexFinder finder = new CrLfByteBufIndexFinder();
        final ByteBuf buffer = Unpooled.copiedBuffer(new byte[] {0x0C, 0x0A});

        Assert.assertFalse(finder.find(buffer, 0));
    }

    /*   i
     *   |
     *   v     0    1
     * --+----+----+----+
     *   |    |0x0D|0x0A|
     * --+----+----+----+
     *        ^         ^
     *        |         |
     *        r         w
     */
    /**
     * Test case.
     */
    @Test
    public void findWithNegativeIndex() {
        final ByteBufIndexFinder finder = new CrLfByteBufIndexFinder();
        final ByteBuf buffer = Unpooled.copiedBuffer(new byte[] {0x0D, 0x0A});

        Assert.assertFalse(finder.find(buffer, -1));
    }

    /*      i
     *      |
     *  0   v1
     * +----+----+
     * |0x0D|0x0A|
     * +----+----+
     * ^         ^
     * |         |
     * r         w
     */
    /**
     * Test case.
     */
    @Test
    public void findInTooSmallReadableChunk() {
        final ByteBufIndexFinder finder = new CrLfByteBufIndexFinder();
        final ByteBuf buffer = Unpooled.copiedBuffer(new byte[] {0x0D, 0x0A});
        buffer.readerIndex(1);

        Assert.assertFalse(finder.find(buffer, 1));
    }

    /* i
     * |
     * v0    1
     * +----+----+
     * |0x0D|0x0A|
     * +----+----+
     *      ^    ^
     *      |    |
     *      r    w
     */
    /**
     * Test case.
     */
    @Test
    public void findWithIndexSmallerReaderIndex() {
        final ByteBufIndexFinder finder = new CrLfByteBufIndexFinder();
        final ByteBuf buffer =
                Unpooled.copiedBuffer(new byte[] {0x0D, 0x0A, 0x00});
        buffer.readerIndex(1);

        Assert.assertFalse(finder.find(buffer, 0));
    }

    /*      i
     *      |
     *  0   v1
     * +----+----+----+
     * |0x00|0x0D|0x0A|
     * +----+----+----+
     * ^         ^
     * |         |
     * r         w
     */
    /**
     * Test case.
     */
    @Test
    public void findInTooSmallIndexChunk() {
        final ByteBufIndexFinder finder = new CrLfByteBufIndexFinder();
        final ByteBuf buffer =
                Unpooled.copiedBuffer(new byte[] {0x00, 0x0D, 0x0A});
        buffer.writerIndex(2);

        Assert.assertFalse(finder.find(buffer, 1));
    }

    /*                i
     *                |
     *  0    1    2   v3
     * +----+----+----+----+
     * |0x0D|0x0A|    |    |
     * +----+----+----+----+
     *      ^    ^
     *      |    |
     *      r    w
     */
    /**
     * Test case.
     */
    @Test
    public void findWithIndexGreaterWriterIndex() {
        final ByteBufIndexFinder finder = new CrLfByteBufIndexFinder();
        final ByteBuf buffer = Unpooled.copiedBuffer(new byte[] {0x0D, 0x0A});
        buffer.readerIndex(1);

        Assert.assertFalse(finder.find(buffer, 3));
    }
}
