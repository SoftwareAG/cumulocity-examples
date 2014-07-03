package com.cumulocity.agent.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.cumulocity.agent.server.config.CommonConfiguration;
import com.cumulocity.agent.server.config.ContextConfiguration;
import com.cumulocity.agent.server.config.CumulocityClientConfiguration;
import com.cumulocity.agent.server.protocol.ProtocolDecoder;
import com.cumulocity.agent.server.protocol.ProtocolEncoder;

public class ServerBuilder {

    private final InetSocketAddress address;

    protected InetSocketAddress address() {
        return address;
    }

    public static ServerBuilder on(final InetSocketAddress address) {
        return new ServerBuilder(address);
    }

    public static ServerBuilder on(final int port) {
        return on(new InetSocketAddress(port));
    }

    private ServerBuilder(InetSocketAddress address) {
        this.address = address;
    }

    protected ServerBuilder(ServerBuilder builder) {
        this.address = builder.address;
    }

    public <T> SingleProtocolServerBuilder protocol(Class<? extends ProtocolDecoder<T>> decoder, Class<? extends ProtocolEncoder<T>> encoder) {
        return new SingleProtocolServerBuilder(decoder, encoder, this);
    }

    public <T> RestServerBuilder rest() {
        return new RestServerBuilder(this);
    }

    public Class[] getAnnotatedClasses() {
        return new Class[] { CommonConfiguration.class};
    }
}
