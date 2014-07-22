package com.cumulocity.agent.server.protocol;

import java.io.InputStream;

public interface ProtocolDecoder<T> {

    T decode(InputStream stream);
}
