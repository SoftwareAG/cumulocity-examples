package c8y.trackeragent_it;

import static org.fest.assertions.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import c8y.Position;
import c8y.SpeedMeasurement;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.rfv16.message.RFV16DeviceMessages;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;

import com.cumulocity.rest.representation.event.EventRepresentation;

public class RFV16ReportIT extends TrackerITSupport {
    
    private String imei;
    private RFV16DeviceMessages deviceMessages = new RFV16DeviceMessages();
    private RFV16ServerMessages serverMessages = new RFV16ServerMessages();

    @Before
    public void init() {
        imei = Devices.randomImei();
    }
    
    @Override
    protected int getLocalPort() {
        return trackerAgentConfig.getLocalPort2();
    }
    
    @Test
    public void shouldProcessPositionUpdateMessage() throws Exception {
        bootstrap(imei, deviceMessages.positionUpdate("DB", imei, Positions.TK10xSample));
        writeInNewConnection(deviceMessages.positionUpdate("DB", imei, Positions.TK10xSample));
        
        assertThat(actualPositionInTracker()).isEqualTo(TK10xCoordinatesTranslator.parse(Positions.TK10xSample));
        assertThat(actualPositionInEvent()).isEqualTo(TK10xCoordinatesTranslator.parse(Positions.TK10xSample));
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
