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

import com.google.common.base.Optional;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A unparsed secure shell protocol version exchange message.
 *
 * @author Matthias Berndt
 */
final class VersionExchangeMessageImpl implements VersionExchangeMessage {

    private static final byte PRINTABLE_ASCII_LOWER_BOUND = 0x20;

    private static final byte PRINTABLE_ASCII_UPPER_BOUND = 0x7E;

    private static final Pattern REGEX =
            Pattern.compile("^SSH-2.0-([\\S&&[^- ]]+)(?: (.+)){0,1}\\r\\n$");

    private Optional<String> comments;

    private String softwareVersion;

    private boolean valid = true;

    /**
     * Construct a version exchange message using a ByteBuf.
     *
     * @param rawMessage
     *   a buffer containing the uparsed message
     */
    VersionExchangeMessageImpl(final ByteBuf rawMessage) {
        validate(rawMessage);
    }

    /* (non-Javadoc)
     * @see net.systemtrap.ssh.VEM#comments()
     */
    @Override
    public Optional<String> comments() {
        if (!isValid()) {
            throw new IllegalStateException();
        }
        return comments;
    }

    /* (non-Javadoc)
     * @see net.systemtrap.ssh.VEM#isValid()
     */
    @Override
    public boolean isValid() {
        return this.valid;
    }

    /* (non-Javadoc)
     * @see net.systemtrap.ssh.VEM#softwareVersion()
     */
    @Override
    public String softwareVersion() {
        if (!isValid()) {
            throw new IllegalStateException();
        }
        return softwareVersion;
    }

    private void checkMessage(final ByteBuf buffer) {
        final String message = buffer.toString(Charset.forName("US-ASCII"));

        final Matcher msgMatch = REGEX.matcher(message);

        if (msgMatch.matches()) {
            setup(msgMatch.group(1), msgMatch.group(2));
        } else {
            invalidate();
        }
    }

    private void checkPrintableAscii(final byte aByte) {
        if ((aByte & 0xFF) < (PRINTABLE_ASCII_LOWER_BOUND & 0xFF) ||
                (aByte & 0xFF) > (PRINTABLE_ASCII_UPPER_BOUND & 0xFF)) {
            invalidate();
        }
    }

    private void checkPrintableAscii(final ByteBuf buffer) {
        final ByteBuf tmpBuffer = buffer.slice();
        final int lowerBound = tmpBuffer.readerIndex();
        final int upperBound = tmpBuffer.writerIndex();

        for (int idx = lowerBound; idx < upperBound; ++idx) {
            checkPrintableAscii(tmpBuffer.getByte(idx));
        }
    }

    private void invalidate() {
        this.valid = false;
    }

    private void setup(final String version, final String comment) {
        softwareVersion = version;
        
        if (comment != null) {
            comments = Optional.of(comment);
        }
    }

    private void validate(final ByteBuf buffer) {
        final int rIdx = buffer.readerIndex();
        final int rLen = buffer.readableBytes();

        final int rLenC = rLen >= 2 ? rLen - 2 : 0;

        checkMessage(buffer.slice());
        checkPrintableAscii(buffer.slice(rIdx, rLenC));
    }
}
