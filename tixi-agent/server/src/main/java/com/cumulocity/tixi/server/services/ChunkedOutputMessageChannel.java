package com.cumulocity.tixi.server.services;

import static com.google.common.base.Throwables.propagate;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.glassfish.jersey.server.ChunkedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closeables;

public class ChunkedOutputMessageChannel<T> implements MessageChannel<T> {
    
    private static final  Logger log = LoggerFactory.getLogger(ChunkedOutputMessageChannel.class);

    private final ChunkedOutput<T> chunkedOutput;

    private final Collection<MessageChannelListener<T>> listeners = new LinkedList<>();

    public ChunkedOutputMessageChannel(ChunkedOutput<T> chunkedOutput) {
        this.chunkedOutput = chunkedOutput;
    }

    @Override
    public void send(T message) {
        try {
            log.trace("sending messsage {}", message);
            chunkedOutput.write(message);
        } catch (IOException e) {
            for (MessageChannelListener<T> listener : listeners) {
                listener.failed(message);
            }
            close();
        }
    }

    @Override
    public void close() {
        try {
            log.trace("closing channel");
            chunkedOutput.close();
        } catch (IOException e) {
            throw propagate(e);
        }
    }

    @Override
    public void addListener(MessageChannelListener<T> listener) {
        listeners.add(listener);
    }

    @Override
    public boolean isClosed() {
        return chunkedOutput.isClosed();
    }

}
