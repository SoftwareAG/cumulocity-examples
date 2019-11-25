package c8y.trackeragent_it;

import org.assertj.core.api.Assertions;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import c8y.Position;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.protocol.telic.TelicDeviceMessages;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.message.TrackerMessage;

public class TelicReportIT extends TrackerITSupport {
    
    private final TelicDeviceMessages deviceMessages = new TelicDeviceMessages();
    private String imei; 
    
    @Override
    protected TrackingProtocol getTrackerProtocol() {
        return TrackingProtocol.TELIC;
    }
    
    @Before
    public void init() {
        imei = Devices.randomImei();
    }

    @Test
    public void shouldBootstrapNewDeviceAndThenChangeItsLocation() throws Exception {
        bootstrapDevice(imei, deviceMessages.positionUpdate(imei, Positions.ZERO));  
        
        TrackerMessage positionUpdate = deviceMessages.positionUpdate(imei, Positions.SAMPLE_4);
        writeInNewConnection(positionUpdate);
        
        Thread.sleep(1000);
        
        ManagedObjectRepresentation deviceMO = getDeviceMO(imei);
        Assertions.assertThat(deviceMO).isNotNull();
        Positions.assertEqual(deviceMO.get(Position.class), Positions.SAMPLE_4);
    }

}
