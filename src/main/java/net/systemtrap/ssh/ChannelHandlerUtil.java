package net.systemtrap.ssh;

import io.netty.channel.ChannelHandler;

public final class ChannelHandlerUtil {

    public static String generateName(
            final ChannelHandler handler) {
        final String type = handler.getClass().getSimpleName();
        final StringBuilder buf = new StringBuilder(type.length() + 10);
        final int hashCode = System.identityHashCode(handler);
        final String hexHashCode = Long.toHexString(hashCode);

        buf.append(type);
        buf.append("-0");
        buf.append(hexHashCode);
        buf.setCharAt(buf.length() - 9, 'x');
        return buf.toString();
    }
}
