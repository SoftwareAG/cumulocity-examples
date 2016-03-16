package c8y.trackeragent_it;

import static org.fest.assertions.Assertions.assertThat;

import java.net.Socket;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;

import c8y.Position;
import c8y.RFV16Config;
import c8y.SetSosNumber;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.mapping.TrackingProtocol;
import c8y.trackeragent.protocol.rfv16.message.RFV16DeviceMessages;
import c8y.trackeragent.protocol.rfv16.parser.RFV16AlarmType;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;

public class RFV16ReportIT extends TrackerITSupport {
    
    private String imei;
    private RFV16DeviceMessages deviceMessages = new RFV16DeviceMessages();

    @Before
    public void init() {
        imei = Devices.randomImei();
    }
    
    @Override
    protected TrackingProtocol getTrackerProtocol() {
        return TrackingProtocol.RFV16;
    }
    
    @Test
    public void processPositionUpdateMessageV1() throws Exception {
        bootstrapDevice(imei, deviceMessages.positionUpdate("DB", imei, Positions.TK10xSample));
        writeInNewConnection(deviceMessages.positionUpdate("DB", imei, Positions.TK10xSample));
        
        assertThat(actualPositionInTracker()).isEqualTo(TK10xCoordinatesTranslator.parse(Positions.TK10xSample));
        assertThat(actualPositionInEvent()).isEqualTo(TK10xCoordinatesTranslator.parse(Positions.TK10xSample));
    }
    
    @Test
    public void processHeartbeatMessage() throws Exception {
        bootstrapDevice(imei, deviceMessages.positionUpdate("DB", imei, Positions.TK10xSample));
        
        writeInNewConnection(deviceMessages.heartbeat("DB", imei, "FFFDFFFF"));
        
        Thread.sleep(1000);
        assertThat(findAlarm(imei, RFV16AlarmType.LOW_BATTERY)).isNotNull();
    }
    
    @Test
    public void setSosNumber() throws Exception {
        bootstrapDevice(imei, deviceMessages.positionUpdate("DB", imei, Positions.TK10xSample));
        Socket socket = writeInNewConnectionAndKeepOpen(deviceMessages.heartbeat("DB", imei, "FFFDFFFF").asBytes());
        TrackerDevice device = getTrackerDevice(imei);
        DeviceControlApi deviceControlApi = trackerPlatform.getDeviceControlApi();
        OperationRepresentation operation = new OperationRepresentation();
        operation.setDeviceId(device.getGId());
        operation.set(new SetSosNumber("112"));
        
        deviceControlApi.create(operation);
        Thread.sleep(11000);
        
        TrackerDevice trackerDevice = getTrackerDevice(imei);
        RFV16Config rfv16Config = trackerDevice.getRFV16Config();
        assertThat(rfv16Config.getSosNumber()).isEqualTo("112");
        socket.close();
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
