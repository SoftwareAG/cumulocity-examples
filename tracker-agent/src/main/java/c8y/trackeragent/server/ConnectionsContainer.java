/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package c8y.trackeragent.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class ConnectionsContainer {

    private final Map<ConnectionDetails, ActiveConnection> connectionsIndex = new ConcurrentHashMap<ConnectionDetails, ActiveConnection>();
    private final List<ActiveConnection> connections = new ArrayList<ActiveConnection>();

    private int channelStatesPos = 0;

    public ActiveConnection get(ConnectionDetails connectionDetails) {
        return connectionsIndex.get(connectionDetails);
    }

    public void put(ConnectionDetails key, ActiveConnection value) {
        connectionsIndex.put(key, value);
        connections.add(value);
    }

    public ActiveConnection next() {
        if (connections.size() == 0) {
            return null;
        }
        channelStatesPos = (channelStatesPos + 1) % connections.size();
        ActiveConnection result = connections.get(channelStatesPos);
        if (result.isProcessing()) {
            return null;
        }
        result.setProcessing(true);
        return result;
    }

    public ActiveConnection getActiveConnection(String imei) {
        for (ActiveConnection connection : connections) {
            if (imei.equals(connection.getConnectionDetails().getImei())) {
                return connection;
            }
        }
        return null;
    }

    public void removeForImei(String imei) {
        // TODO Auto-generated method stub
    }

    protected List<ActiveConnection> getConnections() {
        return connections;
    }

    protected Map<ConnectionDetails, ActiveConnection> getConnectionsIndex() {
        return connectionsIndex;
    }

    public void remove(ConnectionDetails connectionDetails) {
        ActiveConnection activeConnection = connectionsIndex.get(connectionDetails);
        connections.remove(activeConnection);
        connectionsIndex.remove(connectionDetails);
        
    }
    
    
}
