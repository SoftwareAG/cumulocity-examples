package c8y.trackeragent_it;

import org.junit.Test;

import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TelicReports;

public class TelicReportIT extends TrackerITSupport {

    @Test
    public void shouldBootstrapNewDeviceAndThenChangeItsLocation() throws Exception {
        String imei = Devices.randomImei();
        System.out.println("imei " + imei);
        bootstrap(imei, TelicReports.getTelicReportBytes(imei, Positions.ZERO));  
        
        //trigger regular report 
        byte[] report = TelicReports.getTelicReportBytes(imei, Positions.SAMPLE_4);
        writeInNewConnection(report);
        
        Thread.sleep(1000);
        TrackerDevice newDevice = getTrackerDevice(imei);
        Positions.assertEqual(newDevice.getPosition(), Positions.SAMPLE_4);
    }

}
