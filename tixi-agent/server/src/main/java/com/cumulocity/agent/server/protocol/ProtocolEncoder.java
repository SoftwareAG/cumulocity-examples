package com.cumulocity.agent.server.protocol;

import java.io.OutputStream;

public interface ProtocolEncoder<T> {

    void encode(OutputStream stream, T message);
}
