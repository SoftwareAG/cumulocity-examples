package c8y.trackeragent_it;

import org.fest.assertions.Assertions;
import org.junit.Test;

import c8y.trackeragent.utils.CobanDeviceMessages;
import c8y.trackeragent.utils.Devices;

public class CobanReportIT extends TrackerITSupport {
    
    @Test
    public void shouldBootstrapNewDeviceAndSendLoad() throws Exception {
        String imei = Devices.randomImei();
        System.out.println("imei " + imei);
        bootstrap(imei, CobanDeviceMessages.logon(imei));

        // resend logon because no response is send on bootstraping one; 
        String response = writeInNewConnection(CobanDeviceMessages.logon(imei));
        
        Assertions.assertThat(response).isEqualTo("LOAD");
    }


}
