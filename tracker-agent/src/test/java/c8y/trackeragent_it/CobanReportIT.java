package c8y.trackeragent_it;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import c8y.trackeragent.protocol.coban.CobanDeviceMessages;
import c8y.trackeragent.utils.Devices;

public class CobanReportIT extends TrackerITSupport {
    
    @Test
    public void shouldBootstrapNewDeviceAndSendLoad() throws Exception {
        String imei = Devices.randomImei();
        System.out.println("imei " + imei);
        bootstrap(imei, CobanDeviceMessages.logon(imei));

        String response = writeInNewConnection(CobanDeviceMessages.logon(imei));
        
        assertThat(response).isEqualTo("LOAD");
    }
    
    @Test
    public void shouldProcessHeartbeat() throws Exception {
        String imei = Devices.randomImei();
        System.out.println("imei " + imei);
        bootstrap(imei, CobanDeviceMessages.logon(imei));
        
        String response = writeInNewConnection(CobanDeviceMessages.heartbeat(imei));
        
        assertThat(response).isEqualTo("ON");
    }


}
