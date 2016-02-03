package c8y.trackeragent.protocol.rfv16.parser;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;

import c8y.Mobile;
import c8y.Position;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.RFV16ParserTestSupport;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;
import c8y.trackeragent.utils.TrackerConfiguration;
import c8y.trackeragent.utils.message.TrackerMessage;

public class MultiBaseStationRFV16ReportTest extends RFV16ParserTestSupport {
    
    MultiBaseStationDataRFV16Parser parser;
    ArgumentCaptor<RFV16AlarmType> alarmTypeCaptor = ArgumentCaptor.forClass(RFV16AlarmType.class);
    ArgumentCaptor<Mobile> mobileCaptor = ArgumentCaptor.forClass(Mobile.class);
    TrackerConfiguration trackerConfiguration = new TrackerConfiguration();
    
    @Before
    public void init() {
        parser = new MultiBaseStationDataRFV16Parser(trackerAgent, serverMessages, alarmService, trackerConfiguration);
        trackerConfiguration.setRfv16LocationReportTimeInterval(30);
    }
    
    @Test    
    public void shouldParseMultiBaseStationReport() throws Exception {
        when(deviceMock.getManagedObject()).thenReturn(aManagedObjectWithMobileFragment());
        
        TrackerMessage deviceMessage = deviceMessages.multiBaseStationDataReport("DB", IMEI, "262", "1", "16834", "FFFFFFFD");
        ReportContext reportCtx = new ReportContext(deviceMessage.asArray(), IMEI, out);
        
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
