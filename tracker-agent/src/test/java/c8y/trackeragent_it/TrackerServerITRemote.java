package c8y.trackeragent_it;

import org.junit.Ignore;
import org.junit.Test;

import c8y.Position;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TelicReports;

@Ignore
public class TrackerServerITRemote extends TrackerITSupport {
    
    public TrackerServerITRemote() {
        super(REMOTE);
    }

    @Test
    public void shouldBootstrapNewDeviceAndThenChangeItsLocation() throws Exception {
        String imei = Devices.randomImei();
        bootstrap(imei);
                
        //trigger regular report 
        byte[] report = TelicReports.getTelicReportBytes(imei, Positions.SAMPLE_1);
        writeInNewConnection(report);
        
        Thread.sleep(1000);
        TrackerDevice newDevice = getTrackerDevice(imei);
        Position actualPosition = newDevice.getPosition();
        Positions.assertEqual(actualPosition, Positions.SAMPLE_1);
    }
    

}
