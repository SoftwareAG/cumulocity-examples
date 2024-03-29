/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.rfv16.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import c8y.Mobile;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.rfv16.RFV16ParserTestSupport;
import c8y.trackeragent.utils.message.TrackerMessage;

public class MultiBaseStationRFV16ReportTest extends RFV16ParserTestSupport {
    
    MultiBaseStationDataRFV16Parser parser;
    ArgumentCaptor<RFV16AlarmType> alarmTypeCaptor = ArgumentCaptor.forClass(RFV16AlarmType.class);
    ArgumentCaptor<Mobile> mobileCaptor = ArgumentCaptor.forClass(Mobile.class);
    
    @Before
    public void init() {
        parser = new MultiBaseStationDataRFV16Parser(trackerAgent, serverMessages, alarmService);
    }
    
    @Test    
    public void shouldParseMultiBaseStationReport() throws Exception {
        when(deviceMock.getManagedObject()).thenReturn(aManagedObjectWithMobileFragment());
        
        TrackerMessage deviceMessage = deviceMessages.multiBaseStationDataReport("DB", IMEI, "262", "1", "16834", "FFFFFFFD");
        ReportContext reportCtx = new ReportContext(connectionDetails, deviceMessage.asArray());
        
        parser.onParsed(reportCtx);
        
        verify(alarmService).createAlarm(any(ReportContext.class), alarmTypeCaptor.capture(), any(TrackerDevice.class));
        assertThat(alarmTypeCaptor.getValue()).isEqualTo(RFV16AlarmType.SOS);
    }
    
    private static ManagedObjectRepresentation aManagedObjectWithMobileFragment() {
        ManagedObjectRepresentation mo = new ManagedObjectRepresentation();
        Mobile mobile = new Mobile();
        mobile.setImei("12345");
        mo.set(mobile);
        return mo;
    }
}
