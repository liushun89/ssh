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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.testng.annotations.Test;

public class BinaryPacketSplitterTest {

    @Test
    public void simple0() {
        final BinaryPacketSplitter handler = new BinaryPacketSplitter();

        final ByteBuf buffer = Unpooled.buffer();

        buffer.writeInt(0x10);         // packet length
        buffer.writeByte(0x05);        // padding length
        buffer.writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00);        // payload
        buffer.writeByte(0xFF)
              .writeByte(0xFF)
              .writeByte(0xFF)
              .writeByte(0xFF)
              .writeByte(0xFF);        // padding

        final RawBinaryPacket packet = handler.decode(null, buffer);

        assertNotNull(packet);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void simple1() {
        final BinaryPacketSplitter handler = new BinaryPacketSplitter();

        final ByteBuf buffer = Unpooled.buffer();

        buffer.writeInt(0x0FFFFF);     // packet length
        buffer.writeByte(0x05);        // padding length
        buffer.writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00);        // payload
        buffer.writeByte(0xFF)
              .writeByte(0xFF)
              .writeByte(0xFF)
              .writeByte(0xFF)
              .writeByte(0xFF);        // padding

        final RawBinaryPacket packet = handler.decode(null, buffer);

        assertNull(packet);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void simple2() {
        final BinaryPacketSplitter handler = new BinaryPacketSplitter();

        final ByteBuf buffer = Unpooled.buffer();

        buffer.writeInt(0x00);     // packet length
        buffer.writeByte(0x05);        // padding length
        buffer.writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00);        // payload
        buffer.writeByte(0xFF)
              .writeByte(0xFF)
              .writeByte(0xFF)
              .writeByte(0xFF)
              .writeByte(0xFF);        // padding

        final RawBinaryPacket packet = handler.decode(null, buffer);

        assertNull(packet);
    }

    @Test
    public void simple3() {
        final BinaryPacketSplitter handler = new BinaryPacketSplitter();

        final ByteBuf buffer = Unpooled.buffer();

        buffer.writeInt(0x10);         // packet length
        buffer.writeByte(0x05);        // padding length
        buffer.writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00);        // payload
        buffer.writeByte(0xFF)
              .writeByte(0xFF)
              .writeByte(0xFF)
              .writeByte(0xFF)
              .writeByte(0xFF);        // padding

        final RawBinaryPacket packet = handler.decode(null, buffer);

        assertNull(packet);
    }

    @Test
    public void simple4() {
        final BinaryPacketSplitter handler = new BinaryPacketSplitter();

        final ByteBuf buffer = Unpooled.buffer();

        buffer.writeByte(0x00)
              .writeByte(0x00)
              .writeByte(0x00);         // packet length

        final RawBinaryPacket packet = handler.decode(null, buffer);

        assertNull(packet);
    }
}
