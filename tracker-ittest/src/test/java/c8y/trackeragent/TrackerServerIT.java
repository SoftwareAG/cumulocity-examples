package c8y.trackeragent;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import c8y.Position;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.Reports;

public class TrackerServerIT extends TrackerITSupport {

    @Test
    public void shouldBootstrapNewDeviceAndThenChangeItsLocation() throws Exception {
        String imei = Devices.randomImei();
        createNewDeviceRequest(imei);
        byte[] report = Reports.getTelicReportBytes(imei, Positions.ZERO, Positions.SAMPLE_1, Positions.SAMPLE_2, Positions.SAMPLE_3);
        
        //trigger bootstrap
        writeToSocket(report);
        Thread.sleep(5000);
        acceptNewDeviceRequest(imei);
        Thread.sleep(5000);
        
        DeviceCredentials credentials = pollCredentials(imei);
        assertThat(credentials).isNotNull();  
        
        //trigger regular report 
        report = Reports.getTelicReportBytes(imei, Positions.SAMPLE_4);
        writeToSocket(report);
        
        Thread.sleep(1000);
        TrackerDevice newDevice = getTrackerDevice(imei);
        Position actualPosition = newDevice.getPosition();
        Positions.assertEqual(actualPosition, Positions.SAMPLE_4);
    }
    

}
