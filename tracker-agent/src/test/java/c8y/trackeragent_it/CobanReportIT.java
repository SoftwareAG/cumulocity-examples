package c8y.trackeragent_it;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.protocol.coban.CobanDeviceMessages;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;

public class CobanReportIT extends TrackerITSupport {
    
    private String imei;

    @Before
    public void init() {
        imei = Devices.randomImei();
    }
    
    @Test
    public void shouldProcessLoadMessage() throws Exception {
        bootstrap(imei, CobanDeviceMessages.logon(imei));

        String response = writeInNewConnection(CobanDeviceMessages.logon(imei));
        
        assertThat(response).isEqualTo("LOAD");
    }
    
    @Test
    public void shouldProcessHeartbeatMessage() throws Exception {
        bootstrap(imei, CobanDeviceMessages.logon(imei));
        
        String response = writeInNewConnection(CobanDeviceMessages.heartbeat(imei));
        
        assertThat(response).isEqualTo("ON");
    }
    
    @Test
    public void shouldProcessPositionUpdateMessage() throws Exception {
        bootstrap(imei, CobanDeviceMessages.logon(imei));
        
        writeInNewConnection(CobanDeviceMessages.positionUpdate(imei, Positions.SAMPLE_1));
        
        assertThat(getTrackerDevice(imei).getPosition()).isEqualTo(Positions.SAMPLE_1);
    }


}
