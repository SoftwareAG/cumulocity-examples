package c8y.trackeragent_it;

import static org.fest.assertions.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import c8y.Position;
import c8y.SpeedMeasurement;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.coban.CobanConstants;
import c8y.trackeragent.protocol.coban.CobanDeviceMessages;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.protocol.coban.parser.CobanAlarmType;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;
import c8y.trackeragent.utils.message.TrackerMessage;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;

public class CobanReportIT extends TrackerITSupport {
    
    private String imei;
    private CobanDeviceMessages deviceMessages = new CobanDeviceMessages();
    private CobanServerMessages serverMessages = new CobanServerMessages();

    @Before
    public void init() {
        imei = Devices.randomImei();
    }
    
    @Override
    protected int getLocalPort() {
        return trackerAgentConfig.getLocalPort1();
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
        
        assertThat(actualPositionInTracker()).isEqualTo(TK10xCoordinatesTranslator.parse(Positions.TK10xSample));
        assertThat(actualPositionInEvent()).isEqualTo(TK10xCoordinatesTranslator.parse(Positions.TK10xSample));
    }
    
    @Test
    public void shouldProcessSpeedWithinPositionUpdateMessage() throws Exception {
        bootstrap(imei, deviceMessages.logon(imei));
        
        writeInNewConnection(deviceMessages.logon(imei), deviceMessages.positionUpdate(imei, 65));
        
        assertThat(actualSpeedInEvent()).isEqualTo(new BigDecimal(65));
    }

    @Test
    public void shouldProcessAlarmMessage() throws Exception {
        bootstrap(imei, deviceMessages.logon(imei));
        
        writeInNewConnection(deviceMessages.logon(imei), deviceMessages.alarm(imei, CobanAlarmType.LOW_BATTERY));
        
        assertThat(getTrackerDevice(imei).findActiveAlarm(CobanAlarmType.LOW_BATTERY.asC8yType())).isNotNull();
    }
    
    @Test
    public void shouldProcessPositionUpdateNoGpsSignalMessage() throws Exception {
        bootstrap(imei, deviceMessages.logon(imei));
        
        writeInNewConnection(deviceMessages.logon(imei), deviceMessages.positionUpdateNoGPS(imei));
        
        assertThat(getTrackerDevice(imei).findActiveAlarm(CobanAlarmType.NO_GPS_SIGNAL.asC8yType())).isNotNull();
    }
    
    @Test
    public void shouldClearNoGpsSignalAlarm() throws Exception {
        bootstrap(imei, deviceMessages.logon(imei));
        
        writeInNewConnection(deviceMessages.logon(imei), deviceMessages.positionUpdateNoGPS(imei));
        writeInNewConnection(deviceMessages.logon(imei), deviceMessages.positionUpdate(imei, Positions.TK10xSample));
        
        assertThat(getTrackerDevice(imei).findActiveAlarm(CobanAlarmType.NO_GPS_SIGNAL.asC8yType())).isNull();
    }
    
    @Test
    public void shouldProcessOverSpeedMessage() throws Exception {
        bootstrap(imei, deviceMessages.logon(imei));
        
        writeInNewConnection(deviceMessages.logon(imei), deviceMessages.overSpeedAlarm(imei, 50));
        
        AlarmRepresentation alarm = getTrackerDevice(imei).findActiveAlarm(CobanAlarmType.OVERSPEED.asC8yType());
        assertThat(alarm).isNotNull();
        assertThat(alarm.getText()).isEqualTo("Geschwindigkeitsüberschreitung 50km/h");
    }
    
    private BigDecimal actualSpeedInEvent() {
        return actualPositionEvent().get(SpeedMeasurement.class).getSpeed().getValue();
    }
    
    private Position actualPositionInEvent() {
        return actualPositionEvent().get(Position.class);
    }

    private EventRepresentation actualPositionEvent() {
        return getTrackerDevice(imei).findLastEvent(TrackerDevice.LU_EVENT_TYPE);
    }
    
    private Position actualPositionInTracker() {
        return getTrackerDevice(imei).getPosition();
    }

}
