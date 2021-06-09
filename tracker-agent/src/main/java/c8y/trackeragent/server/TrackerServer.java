/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import c8y.trackeragent.server.TrackerServerEvent.CloseConnectionEvent;
import c8y.trackeragent.server.TrackerServerEvent.ReadDataEvent;
import c8y.trackeragent.utils.ByteHelper;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TrackerServer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TrackerServer.class);

    // The channel on which we'll accept connections
    private ServerSocketChannel serverChannel;

    // The selector we'll be monitoring
    private Selector selector;

    // The buffer into which we'll read data when it's available
    private final ByteBuffer readBuffer = ByteBuffer.allocate(8192);

    // A list of PendingChange instances
    private final List<ChangeRequest> pendingChanges = new LinkedList<ChangeRequest>();

    // Maps a SocketChannel to a list of ByteBuffer instances
    private final Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<SocketChannel, List<ByteBuffer>>();

    private final TrackerServerEventHandler eventHandler;

    @Autowired
    public TrackerServer(TrackerServerEventHandler eventHandler) throws IOException {
        this.eventHandler = eventHandler;
    }

    public void start(int port) throws IOException {
        if (serverChannel != null) {
            throw new IllegalStateException("The instance of " + this.getClass().getSimpleName() + " have been already started");
        }
        logger.info("Will start listening on the port: {}", port);
        // Create a new non-blocking server socket channel
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        // Bind the server socket to the specified port
        InetSocketAddress isa = new InetSocketAddress(port);
        serverChannel.socket().bind(isa);

        // Register the server socket channel, indicating an interest in
        // accepting new connections
        this.selector = SelectorProvider.provider().openSelector();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        logger.info("Started listening on the port: {}", port);
    }

    public void send(SocketChannel socket, String text) {
        logger.info("Enqueue text to device: {}", text);
        final byte[] data = ByteHelper.getBytes(text);
        synchronized (this.pendingChanges) {
            // Indicate we want the interest ops set changed
            this.pendingChanges.add(new ChangeRequest(socket, SelectionKey.OP_WRITE));

            // And queue the data we want written
            synchronized (this.pendingData) {
                List<ByteBuffer> queue = this.pendingData.computeIfAbsent(socket, k -> new ArrayList<>());
                queue.add(ByteBuffer.wrap(data));
            }
        }

        // Finally, wake up our selecting thread so it can make the required
        // changes
        this.selector.wakeup();
    }

    public void close() throws IOException {
        serverChannel.close();
    }

    public void run() {
        while (true) {
            logger.trace("Process main server loop");
            try {
                // Process any pending changes
                synchronized (this.pendingChanges) {
                    for (ChangeRequest change : this.pendingChanges) {
                        processPendingChange(change);
                    }
                    logger.trace("Finished pending changes loop");
                    this.pendingChanges.clear();
                }

                // Wait for an event one of the registered channels
                this.selector.select();

                // Iterate over the set of keys for which events are available
                Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    logger.trace("Process reading selectors loop");
                    SelectionKey key = (SelectionKey) selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    // Check what event is available and deal with it
                    if (key.isAcceptable()) {
                        this.accept(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    } else if (key.isWritable()) {
                        this.write(key);
                    }
                }
            } catch (Exception e) {
                logger.error("Error in main server loop", e);
            }
        }
    }

    private void processPendingChange(ChangeRequest change) {
        try {
            logger.trace("Process pending change: {}", change);
            SelectionKey key = change.getSocket().keyFor(this.selector);
            if (key == null) {
                logger.info("The channel is not currently registered with the selector. Connection was probably closed. Ignore change: {}.",
                        change);
                return;
            }
            if (!key.isValid()) {
                logger.info("The channel is not valid. Ignore change: {}.", change);
                return;
            }
            key.interestOps(change.getOps());
        } catch (Exception ex) {
            logger.error("Exception thrown; ignore change: " + change, ex);
        }
    }

    private void accept(SelectionKey key) throws IOException {
        logger.info("Received new connection acceptance request.");
        // For an accept to be pending the channel must be a server socket
        // channel.
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        // Accept the connection and make it non-blocking
        SocketChannel socketChannel = serverSocketChannel.accept();
        if (socketChannel == null) {
            logger.info("Do no accept new connection since it was from not blocking channel.");
            return;
        }
        logger.info("Accept new connection: {}.", socketChannel);
        socketChannel.configureBlocking(false);

        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's data waiting to be read
        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        // Clear out our read buffer so it's ready for new data
        this.readBuffer.clear();

        // Attempt to read off the channel
        int numRead;
        try {
            numRead = channel.read(this.readBuffer);
        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            key.cancel();
            channel.close();
            return;
        }

        if (numRead == -1) {
            logger.info("Remote entity shut the socket {} down cleanly", channel);
            // Remote entity shut the socket down cleanly. Do the
            // same from our end and cancel the channel.
            key.channel().close();
            key.cancel();
            eventHandler.handle(new CloseConnectionEvent(this, channel));
            return;
        }

        // Hand the data off to our worker thread
        eventHandler.handle(new ReadDataEvent(this, channel, this.readBuffer.array(), numRead));
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        synchronized (this.pendingData) {
            try {
                List<ByteBuffer> queue = this.pendingData.get(socketChannel);
                write(socketChannel, queue);
                if (queue.isEmpty()) {
                    // We wrote away all data, so we're no longer interested
                    // in writing on this socket. Switch back to waiting for
                    // data.
                    key.interestOps(SelectionKey.OP_READ);
                }
            } catch (Exception ex) {
                logger.error("Cannot read to channel " + socketChannel + "; delete content from pendingData", ex);
                this.pendingData.remove(socketChannel);
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    private void write(SocketChannel socketChannel, List<ByteBuffer> queue) throws IOException {
        // Write until there's not more data ...
        while (!queue.isEmpty()) {
            ByteBuffer buf = (ByteBuffer) queue.get(0);
            socketChannel.write(buf);
            if (buf.remaining() > 0) {
                // ... or the socket's buffer fills up
                break;
            }
            queue.remove(0);
        }
    }

}
