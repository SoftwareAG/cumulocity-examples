package c8y.trackeragent;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import org.junit.Test;

import c8y.Position;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.Reports;

public class TrackerServerIT extends TrackerITSupport {

    @Test
    public void shouldBootstrapNewDeviceAndThenChangeItsLocation() throws Exception {
        testShouldBootstrapNewDeviceAndThenChangeItsLocation();
    }

    private void testShouldBootstrapNewDeviceAndThenChangeItsLocation() throws UnsupportedEncodingException, Exception, InterruptedException {
        String imei = Devices.randomImei();
        createNewDeviceRequest(imei);
        byte[] report = Reports.getTelicReportBytes(imei, Positions.ZERO, Positions.SAMPLE_1, Positions.SAMPLE_2, Positions.SAMPLE_3);
        
        //trigger bootstrap
        writeInNewConnection(report);
        Thread.sleep(8000);
        acceptNewDeviceRequest(imei);
        Thread.sleep(15000);
        
        DeviceCredentials credentials = pollCredentials(imei);
        assertThat(credentials).isNotNull();  
        
        //trigger regular report 
        report = Reports.getTelicReportBytes(imei, Positions.SAMPLE_4);
        writeInNewConnection(report);
        
        Thread.sleep(1000);
        TrackerDevice newDevice = getTrackerDevice(imei);
        Position actualPosition = newDevice.getPosition();
        Positions.assertEqual(actualPosition, Positions.SAMPLE_4);
    }
    
    @Test
    public void shouldHandleTimeoutOnConnection() throws Exception {
        timeoutConnection();
        testShouldBootstrapNewDeviceAndThenChangeItsLocation();
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
