/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.server;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.server.writer.OutWriter;

public class ConnectionDetails {

    private final TrackingProtocol trackingProtocol;
    private final OutWriter outWriter;
    private final SocketChannel channel;
    private volatile String imei;
    private final Map<String, Object> params = new ConcurrentHashMap<String, Object>();
    private ConnectionsContainer connectionsContainer;

    public ConnectionDetails(TrackingProtocol trackingProtocol, OutWriter outWriter, SocketChannel channel,
                             ConnectionsContainer connectionsContainer) {
        this(trackingProtocol, outWriter, channel);
        this.connectionsContainer = connectionsContainer;
    }

    public ConnectionDetails(TrackingProtocol trackingProtocol, OutWriter outWriter, SocketChannel channel) {
        this.trackingProtocol = trackingProtocol;
        this.outWriter = outWriter;
        this.channel = channel;
    }

    public OutWriter getOutWriter() {
        return outWriter;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public synchronized void setImei(String imei) {
        if (this.imei == null) {
            this.imei = imei;
        } else if (!this.imei.equals(imei)) {
            throw new RuntimeException("Imei " + this.imei + " already bound to the connection! Cant rebind to " + imei + "!");
        }
        if (connectionsContainer != null) {
            connectionsContainer.register(imei, channel);
        }
    }

    public synchronized void resetImei() {
        imei = null;
    }

    public String getImei() {
        return imei;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public TrackingProtocol getTrackingProtocol() {
        return trackingProtocol;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConnectionDetails other = (ConnectionDetails) obj;
        if (channel == null) {
            if (other.channel != null)
                return false;
        } else if (!channel.equals(other.channel))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConnectionDetails [channel=");
        builder.append(channel);
        builder.append(", trackingProtocol=");
        builder.append(trackingProtocol);
        builder.append(", imei=");
        builder.append(imei);
        builder.append("]");
        return builder.toString();
    }



    
}
