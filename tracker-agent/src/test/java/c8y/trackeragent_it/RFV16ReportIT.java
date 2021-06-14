/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent_it;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.rest.representation.event.EventRepresentation;

import c8y.Position;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.TrackingProtocol;
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
    
    private Position actualPositionInEvent() {
        return actualPositionEvent().get(Position.class);
    }

    private EventRepresentation actualPositionEvent() {
        return findLastEvent(imei, TrackerDevice.LU_EVENT_TYPE);
    }
    
    private Position actualPositionInTracker() {
        return getDeviceMO(imei).get(Position.class);
    }

}
