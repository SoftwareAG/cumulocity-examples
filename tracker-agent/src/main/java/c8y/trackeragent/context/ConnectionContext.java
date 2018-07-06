package c8y.trackeragent.context;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.server.ConnectionDetails;
import c8y.trackeragent.utils.message.TrackerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;

public class ConnectionContext {

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(ConnectionContext.class);

    private final ConnectionDetails connectionDetails;

    public ConnectionContext(ConnectionDetails connectionDetails) {
        this.connectionDetails = connectionDetails;
    }

    public Object getConnectionParam(String paramName) {
        return connectionDetails.getParams().get(paramName);
    }

    public boolean isConnectionFlagOn(String paramName) {
        return Boolean.TRUE.equals(getConnectionParam(paramName));
    }

    public void setConnectionParam(String paramName, Object paramValue) {
        connectionDetails.getParams().put(paramName, paramValue);
    }

    public String getImei() {
        return connectionDetails.getImei();
    }

    public TrackingProtocol getTrackingProtocol() {
        return connectionDetails.getTrackingProtocol();
    }

    public void setImei(String imei) {
        connectionDetails.setImei(imei);
    }

    public SocketChannel getConnection() {
        return connectionDetails.getChannel();
    }

    public DeviceContext getDeviceContext() {
        return DeviceContextRegistry.get().get(getImei());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connectionDetails == null) ? 0 : connectionDetails.hashCode());
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
        ConnectionContext other = (ConnectionContext) obj;
        if (connectionDetails == null) {
            if (other.connectionDetails != null)
                return false;
        } else if (!connectionDetails.equals(other.connectionDetails))
            return false;
        return true;
    }

    public void writeOut(String text) {
        connectionDetails.getOutWriter().write(text);

    }

    public void writeOut(TrackerMessage msg) {
        writeOut(msg.asText());
    }
}
