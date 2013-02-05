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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

import org.testng.annotations.Test;

public final class VersionExchangeMessageTest {

    /**
     * Test case.
     */
    @Test
    public void simple0() {
        final String msg = "SSH-2.0-1 a\r\n";
        final ByteBuf buffer =
                Unpooled.copiedBuffer(msg, Charset.defaultCharset());

        final VersionExchangeMessage message =
                new VersionExchangeMessageImpl(buffer);

        assertNotNull(message);
        assertTrue(message.isValid());
        assertEquals(message.softwareVersion(), "1");
        assertEquals(message.comments().get(), "a");
    }

    /**
     * Test case.
     */
    @Test
    public void simple1() {
        byte[] alloc =
            {0x00, (byte) 0xFF, 0x0D, 0x0A};
        final ByteBuf buffer = Unpooled.wrappedBuffer(alloc);

        final VersionExchangeMessage message =
                new VersionExchangeMessageImpl(buffer);

        assertNotNull(message);
        assertFalse(message.isValid());
    }

    /**
     * Test case.
     */
    @Test
    public void simple2() {
        byte[] alloc =
            {0x0D, 0x0A};
        final ByteBuf buffer = Unpooled.wrappedBuffer(alloc);

        final VersionExchangeMessage message =
                new VersionExchangeMessageImpl(buffer);

        assertNotNull(message);
        assertFalse(message.isValid());
    }

    /**
     * Test case.
     */
    @Test
    public void simple3() {
        byte[] alloc =
            {0x0A};
        final ByteBuf buffer = Unpooled.wrappedBuffer(alloc);

        final VersionExchangeMessage message =
                new VersionExchangeMessageImpl(buffer);

        assertNotNull(message);
        assertFalse(message.isValid());
    }

    /**
     * Test case.
     */
    @Test
    public void simple4() {
        final String msg = "SSH-2.0-1\r\n";
        final ByteBuf buffer =
                Unpooled.copiedBuffer(msg, Charset.defaultCharset());

        final VersionExchangeMessage message =
                new VersionExchangeMessageImpl(buffer);

        assertNotNull(message);
        assertTrue(message.isValid());
    }

    /**
     * Test case.
     */
    @Test(expectedExceptions = IllegalStateException.class)
    public void simple5() {
        byte[] alloc =
            {0x0D, 0x0A};
        final ByteBuf buffer = Unpooled.wrappedBuffer(alloc);

        final VersionExchangeMessage message =
                new VersionExchangeMessageImpl(buffer);

        assertNotNull(message);
        assertFalse(message.isValid());
        
        message.softwareVersion();
    }

    /**
     * Test case.
     */
    @Test(expectedExceptions = IllegalStateException.class)
    public void simple6() {
        byte[] alloc =
            {0x0D, 0x0A};
        final ByteBuf buffer = Unpooled.wrappedBuffer(alloc);

        final VersionExchangeMessage message =
                new VersionExchangeMessageImpl(buffer);

        assertNotNull(message);
        assertFalse(message.isValid());
        
        message.comments();
    }
}
