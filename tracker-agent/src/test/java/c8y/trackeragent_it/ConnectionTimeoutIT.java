package c8y.trackeragent_it;

import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.mapping.TrackingProtocol;
import c8y.trackeragent.protocol.telic.TelicDeviceMessages;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.message.TrackerMessage;

public class ConnectionTimeoutIT extends TrackerITSupport {
    
    private final TelicDeviceMessages deviceMessages = new TelicDeviceMessages(); 
    
    @Override
    protected TrackingProtocol getTrackerProtocol() {
        return TrackingProtocol.TELIC;
    }

    @Test
    public void shouldHandleTimeoutOnConnection() throws Exception {
        timeoutConnection();
        
        String imei = Devices.randomImei();
        bootstrapDevice(imei, deviceMessages.positionUpdate(imei, Positions.ZERO));  
        
        // trigger regular report 
        TrackerMessage message = deviceMessages.positionUpdate(imei, Positions.SAMPLE_4);
        writeInNewConnection(message);
        
        Thread.sleep(1000);
        TrackerDevice newDevice = getTrackerDevice(imei);
        Positions.assertEqual(newDevice.getPosition(), Positions.SAMPLE_4);
    }

    private void timeoutConnection() throws IOException, InterruptedException {
        int defaultClientTimeout = trackerAgentConfig.getClientTimeout();
        trackerAgentConfig.setClientTimeout(1000);
        Socket socket = newSocket();
        socket.getOutputStream();
        Thread.sleep(2 * 1000);
        trackerAgentConfig.setClientTimeout(defaultClientTimeout);
    }


}
