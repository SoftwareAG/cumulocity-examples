package com.cumulocity.tixi.server.resources;

import java.io.IOException;

import org.glassfish.jersey.server.ChunkedOutput;

import com.cumulocity.tixi.server.services.MessageChannel;
import com.cumulocity.tixi.server.services.MessageChannelContext;
import com.google.common.io.Closeables;

public class ChunkedOutputMessageChannel<T> implements MessageChannel<T> {

    private final ChunkedOutput<T> chunkedOutput;

    public ChunkedOutputMessageChannel(ChunkedOutput<T> chunkedOutput) {
        this.chunkedOutput = chunkedOutput;
    }

    @Override
    public void send(MessageChannelContext context, T message) {
        try {
            chunkedOutput.write(message);
        } catch (IOException e) {
            try {
                Closeables.close(chunkedOutput, true);
                Closeables.close(context, true);
            } catch (IOException ex) {
            }
        }
    }

}
