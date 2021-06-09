/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.server.writer;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.server.TrackerServer;

public class OutWriterImpl implements OutWriter {
    
    private static Logger logger = LoggerFactory.getLogger(OutWriter.class);

    private TrackerServer server;
    private SocketChannel channel;

    public OutWriterImpl(TrackerServer server, SocketChannel channel) {
        this.server = server;
        this.channel = channel;
    }

    @Override
    public void write(String text) {
        logger.debug("Write to device: {}.", text);
        server.send(channel, text);
    }

}
