package c8y.trackeragent.protocol.telic;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.cumulocity.rest.representation.event.EventRepresentation;

import c8y.Position;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.context.TrackerContext;
import c8y.trackeragent.protocol.telic.parser.LogCodeType;
import c8y.trackeragent.protocol.telic.parser.TelicLocationReport;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TelicReports;

public class TelicLocationReportTest {
    
    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerContext trackerContext = mock(TrackerContext.class);
    private TrackerDevice device = mock(TrackerDevice.class);
    private TelicLocationReport telic = new TelicLocationReport(trackerAgent);
    private ArgumentCaptor<EventRepresentation> locationEventCaptor = ArgumentCaptor.forClass(EventRepresentation.class); 
    private ArgumentCaptor<Position> positionCaptor = ArgumentCaptor.forClass(Position.class); 

    @Before
    public void setup() throws Exception {
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);
        when(trackerAgent.getContext()).thenReturn(trackerContext);
    }
    
    private void verifyReport() {
        verify(trackerAgent).getOrCreateTrackerDevice(Devices.IMEI_1);
        verify(device).setPosition(locationEventCaptor.capture(), positionCaptor.capture());
    }
    
    @Test
    public void shouldParseImei() throws Exception {
        String[] report = TelicReports.getTelicReport(Devices.IMEI_1, Positions.SAMPLE_1);
        
        String actualImei = telic.parse(report);
        
        assertThat(actualImei).isEqualTo(Devices.IMEI_1);
    }
    
    @Test
    public void shouldUpdateDevicePosition() throws Exception {
        String[] report = TelicReports.getTelicReport(Devices.IMEI_1, Positions.SAMPLE_1);
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verifyReport();
        assertThat(positionCaptor.getValue()).isEqualTo(Positions.SAMPLE_1);
    }
    
    @Test
    public void shouldSetLogCodeTypeInPositionFragment() throws Exception {
        String[] report = TelicReports.getTelicReport(Devices.IMEI_1, Positions.SAMPLE_1);
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verifyReport();
        Object actualLogCOdeType = positionCaptor.getValue().getProperty(TelicConstants.LOG_CODE_TYPE);
        assertThat(actualLogCOdeType).isEqualTo(LogCodeType.TIME_EVENT.getLabel());
    }
   

}
