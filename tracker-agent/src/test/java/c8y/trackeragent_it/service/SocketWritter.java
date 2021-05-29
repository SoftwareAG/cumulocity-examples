/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent_it.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.utils.ByteHelper;
import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent_it.TestSettings;

public class SocketWritter {

    private static Logger logger = LoggerFactory.getLogger(SocketWritter.class);

    // private static final Collection<Socket> sockets = new HashSet<Socket>();
    private final TestSettings testSettings;
    private Integer port;
    private Socket socket;

    public SocketWritter(TestSettings testSettings, Integer port) {
        this.testSettings = testSettings;
        this.port = port;
    }

    public Socket write(TrackerMessage deviceMessage) throws Exception {
        return write(deviceMessage.asBytes());
    }

    public Socket write(byte[] bis) throws Exception {
        if (socket == null) {
            socket = newSocket();
        }
        OutputStream out = socket.getOutputStream();
        logger.info("Write >> " + ByteHelper.getString(bis));
        out.write(bis);
        return socket;
    }

    public void closeExistingConnection() throws IOException {
        if(socket != null) {
            socket.close();
            socket = null;
        }
    }

    public String writeInNewConnection(TrackerMessage... deviceMessages) throws Exception {
        TrackerMessage sum = deviceMessages[0];
        for (int index = 1; index < deviceMessages.length; index++) {
            sum = sum.appendReport(deviceMessages[index]);
        }
        logger.info("Send message: {}", sum);
        Socket newSocket = newSocket();
        return writeInNewConnection(newSocket, sum.asBytes());
    }

    private String writeInNewConnection(Socket socket, byte[] bis) throws Exception {
        OutputStream out = socket.getOutputStream();
        logger.info("Write >> " + ByteHelper.getString(bis));
        out.write(bis);
        out.flush();
        Thread.sleep(1000);
        String response = readSocketResponse(socket);
        socket.close();
        return response;
    }

    private String readSocketResponse(Socket socket) throws Exception {
        InputStream in = socket.getInputStream();
        byte[] bytes = new byte[0];
        try {
            int b;
            while ((b = in.read()) >= 0) {
                bytes = ArrayUtils.add(bytes, (byte) b);
            }
        } catch (SocketTimeoutException stex) {
            // nothing to do, simply end of input handled
        }
        return bytes.length == 0 ? null : new String(bytes, "US-ASCII");
    }

    private Socket newSocket() throws IOException {
        String socketHost = testSettings.getTrackerAgentHost();
        try {
            Socket socket = new Socket(socketHost, port);
            socket.setSoTimeout(2000);
            // sockets.add(socket);
            return socket;
        } catch (IOException ex) {
            System.out.println("Cant connect to socket, host = " + socketHost + ", port = " + port);
            throw ex;
        }
    }

}
