package c8y.trackeragent.server;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import c8y.trackeragent.server.writer.OutWriter;

public class ConnectionDetails {

    private final OutWriter outWriter;
    private final SocketChannel channel;
    private volatile String imei;
    private final Map<String, Object> params = new ConcurrentHashMap<String, Object>();

    public ConnectionDetails(OutWriter outWriter, SocketChannel channel) {
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
    }

    public String getImei() {
        return imei;
    }
    
    public Map<String, Object> getParams() {
        return params;
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
        return "ConnectionDetails [imei=" + imei + "]";
    }
    
    

}
