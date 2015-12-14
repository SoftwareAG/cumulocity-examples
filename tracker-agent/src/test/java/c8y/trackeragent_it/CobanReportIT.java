package c8y.trackeragent_it;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.protocol.coban.CobanConstants;
import c8y.trackeragent.protocol.coban.CobanDeviceMessages;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TK10xUtils;
import c8y.trackeragent.utils.message.TrackerMessage;

public class CobanReportIT extends TrackerITSupport {
    
    private String imei;
    private CobanDeviceMessages deviceMessages = new CobanDeviceMessages();
    private CobanServerMessages serverMessages = new CobanServerMessages();

    @Before
    public void init() {
        imei = Devices.randomImei();
    }
    
    @Test
    public void shouldProcessLogonMessage() throws Exception {
        bootstrap(imei, deviceMessages.logon(imei));

        String response = writeInNewConnection(deviceMessages.logon(imei));
        
        TrackerMessage actual = serverMessages.msg(response);
        TrackerMessage expected = serverMessages
                .load()
                .appendReport(serverMessages.timeIntervalLocationRequest(imei, CobanConstants.DEFAULT_LOCATION_REPORT_INTERVAL));
        assertThat(actual).isEqualTo(expected);
    }
    
    @Test
    public void shouldProcessHeartbeatMessage() throws Exception {
        bootstrap(imei, deviceMessages.logon(imei));
        
        String response = writeInNewConnection(deviceMessages.logon(imei), deviceMessages.heartbeat(imei));
        
        TrackerMessage actual = serverMessages.msg(response);
        TrackerMessage expected = serverMessages
                .load()
                .appendReport(serverMessages.timeIntervalLocationRequest(imei, CobanConstants.DEFAULT_LOCATION_REPORT_INTERVAL))
                .appendReport(serverMessages.on());
        assertThat(actual).isEqualTo(expected);
    }
    
    @Test
    public void shouldProcessPositionUpdateMessage() throws Exception {
        bootstrap(imei, deviceMessages.logon(imei));
        
        writeInNewConnection(deviceMessages.logon(imei), deviceMessages.positionUpdate(imei, Positions.TK10xSample));
        
        assertThat(getTrackerDevice(imei).getPosition()).isEqualTo(TK10xUtils.parse(Positions.TK10xSample));
    }


}
