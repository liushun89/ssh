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

import io.netty.bootstrap.Bootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;

import io.netty.channel.aio.AioEventLoopGroup;

import io.netty.channel.socket.aio.AioSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * {@link SshClient} is a helper creating secure shell channels.
 *
 * @author Matthias Berndt
 *
 */
public final class SshClient {

    /**
     * IANA default port number for secure shell server.
     */
    public static final int IANA_SSH_PORT = 22;

    /**
     * The netty.io bootstrap helper to be used.
     */
    private final transient Bootstrap bootstrap;

    /**
     * Ensures the existence of a default constructor.
     */
    public SshClient() {
        this(new Bootstrap());

        this.bootstrap.group(defaultEventLoopGroup());
        this.bootstrap.channel(AioSocketChannel.class);

        this.bootstrap.remoteAddress(defaultRemoteSocketAddress());
    }

    /**
     * Contruct with a given netty client bootstrap.
     *
     * @param clientBootstrap
     *   a netty bootstrap for connection creation
     */
    public SshClient(final Bootstrap clientBootstrap) {
        this.bootstrap = clientBootstrap;
    }

    /**
     * Connect to the remote peer.
     *
     * @return
     *   a channel future
     */
    public ChannelFuture connect() {
        this.bootstrap.handler(defaultChannelHandler());

        return this.bootstrap.connect();
    }

    /**
     * Get a secure shell protocol specific handler.
     *
     * @return
     *   a secure shell protocol specific handler
     */
    private ChannelHandler defaultChannelHandler() {
        return new SshChannelInitializer();
    }

    /**
     * Get the explicitly configured event loop group or as fallback the
     * default event loop group.
     *
     * @return
     *   a event loop group for channel handling
     */
    private EventLoopGroup defaultEventLoopGroup() {
        return new AioEventLoopGroup();
    }

    /**
     * Get the explicitly configured remote host socket address or as fallback
     * the default remote host socket address.
     *
     * @return
     *   a socket address
     */
    private SocketAddress defaultRemoteSocketAddress() {
        final InetAddress inetAddress = InetAddress.getLoopbackAddress();
        final SocketAddress address =
                new InetSocketAddress(inetAddress, IANA_SSH_PORT);

        return address;
    }
}
