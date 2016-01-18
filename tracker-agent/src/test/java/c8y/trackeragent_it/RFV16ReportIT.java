package c8y.trackeragent_it;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import c8y.Position;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.mapping.TrackerProtocol;
import c8y.trackeragent.protocol.rfv16.message.RFV16DeviceMessages;
import c8y.trackeragent.protocol.rfv16.parser.RFV16AlarmType;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;

import com.cumulocity.rest.representation.event.EventRepresentation;

public class RFV16ReportIT extends TrackerITSupport {
    
    private String imei;
    private RFV16DeviceMessages deviceMessages = new RFV16DeviceMessages();

    @Before
    public void init() {
        imei = Devices.randomImei();
    }
    
    @Override
    protected TrackerProtocol getTrackerProtocol() {
        return TrackerProtocol.RFV16;
    }
    
    @Test
    public void processPositionUpdateMessageV1() throws Exception {
        bootstrap(imei, deviceMessages.positionUpdate("DB", imei, Positions.TK10xSample));
        writeInNewConnection(deviceMessages.positionUpdate("DB", imei, Positions.TK10xSample));
        
        assertThat(actualPositionInTracker()).isEqualTo(TK10xCoordinatesTranslator.parse(Positions.TK10xSample));
        assertThat(actualPositionInEvent()).isEqualTo(TK10xCoordinatesTranslator.parse(Positions.TK10xSample));
    }
    
    @Test
    public void processHeartbeatMessage() throws Exception {
        bootstrap(imei, deviceMessages.positionUpdate("DB", imei, Positions.TK10xSample));
        
        writeInNewConnection(deviceMessages.heartbeat("DB", imei, "FFFDFFFF"));
        
        assertThat(getTrackerDevice(imei).findActiveAlarm(RFV16AlarmType.LOW_BATTERY.asC8yType())).isNotNull();
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
