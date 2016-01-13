package c8y.trackeragent_it;

import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TelicReports;

public class ConnectionTimeoutIT extends TrackerITSupport {
    
    @Override
    protected int getLocalPort() {
        return trackerAgentConfig.getLocalPort1();
    }

    @Test
    public void shouldHandleTimeoutOnConnection() throws Exception {
        timeoutConnection();
        
        String imei = Devices.randomImei();
        bootstrap(imei, TelicReports.getTelicReportBytes(imei, Positions.ZERO));  
        
        // trigger regular report 
        byte[] report = TelicReports.getTelicReportBytes(imei, Positions.SAMPLE_4);
        writeInNewConnection(report);
        
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
