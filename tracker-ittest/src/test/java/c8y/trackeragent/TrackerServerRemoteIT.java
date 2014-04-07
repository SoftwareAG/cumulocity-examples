package c8y.trackeragent;

import org.junit.Test;

import c8y.Position;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.Reports;

public class TrackerServerRemoteIT extends TrackerITSupport {
    
    public TrackerServerRemoteIT() {
        super(REMOTE);
    }

    @Test
    public void shouldBootstrapNewDeviceAndThenChangeItsLocation() throws Exception {
        String imei = Devices.randomImei();
        createNewDeviceRequest(imei);
        byte[] report = Reports.getTelicReportBytes(imei, Positions.ZERO);
        
        //trigger bootstrap
        writeToSocket(report);
        Thread.sleep(5000);
        acceptNewDeviceRequest(imei);
        Thread.sleep(5000);
                
        //trigger regular report 
        report = Reports.getTelicReportBytes(imei, Positions.SAMPLE_1);
        writeToSocket(report);
        
        Thread.sleep(1000);
        TrackerDevice newDevice = getTrackerDevice(imei);
        Position actualPosition = newDevice.getPosition();
        Positions.assertEqual(actualPosition, Positions.SAMPLE_1);
    }
    

}
