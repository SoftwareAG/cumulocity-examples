package com.cumulocity.agent.server;

import com.cumulocity.agent.server.protocol.ProtocolDecoder;
import com.cumulocity.agent.server.protocol.ProtocolEncoder;

public class SingleProtocolServerBuilder {

    private final Class<? extends ProtocolDecoder<?>> decoder;

    private final Class<? extends ProtocolEncoder<?>> encoder;

    private ServerBuilder base;

    public SingleProtocolServerBuilder(Class<? extends ProtocolDecoder<?>> decoder, Class<? extends ProtocolEncoder<?>> encoder,
            ServerBuilder base) {
        this.decoder = decoder;
        this.encoder = encoder;
        this.base = base;
    }
}
