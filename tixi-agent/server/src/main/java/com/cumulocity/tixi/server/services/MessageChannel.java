package com.cumulocity.tixi.server.services;

import java.io.Closeable;

public interface MessageChannel<T> extends Closeable {

    void send(MessageChannelContext context, T message);

}
