package c8y.trackeragent_it;

import org.junit.Test;

import c8y.trackeragent.utils.CobanDeviceMessages;
import c8y.trackeragent.utils.Devices;

public class CobanReportIT extends TrackerITSupport {
    
    @Test
    public void shouldBootstrapNewDevice() throws Exception {
        String imei = Devices.randomImei();
        System.out.println("imei " + imei);
        bootstrap(imei, CobanDeviceMessages.logon(imei));  
    }


}
