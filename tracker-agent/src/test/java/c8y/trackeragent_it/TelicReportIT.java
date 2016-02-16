package c8y.trackeragent_it;

import org.junit.Test;

import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.mapping.TrackerProtocol;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TelicReports;

public class TelicReportIT extends TrackerITSupport {
    
    @Override
    protected TrackerProtocol getTrackerProtocol() {
        return TrackerProtocol.TELIC;
    }

    @Test
    public void shouldBootstrapNewDeviceAndThenChangeItsLocation() throws Exception {
        String imei = Devices.randomImei();
        bootstrap(imei, TelicReports.getTelicReportBytes(imei, Positions.ZERO));  
        
        //Thread.sleep(5000);
        //trigger regular report 
        byte[] report = TelicReports.getTelicReportBytes(imei, Positions.SAMPLE_4);
        writeInNewConnection(report);
        
        Thread.sleep(1000);
        TrackerDevice newDevice = getTrackerDevice(imei);
        Positions.assertEqual(newDevice.getPosition(), Positions.SAMPLE_4);
    }

}
