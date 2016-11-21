package c8y.trackeragent.protocol.queclink;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import c8y.Position;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.parser.QueclinkCrashReport;
import c8y.trackeragent.server.TestConnectionDetails;
import c8y.trackeragent.utils.LocationEventBuilder;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;

import static c8y.trackeragent.protocol.TrackingProtocol.QUECLINK;
public class QueclinkCrashReportTest {
    
    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerDevice trackerDevice = mock(TrackerDevice.class);
    private LocationEventBuilder locationEventBuilder = mock(LocationEventBuilder.class);
    private TestConnectionDetails connectionDetails;
    private EventRepresentation locationEventRepresentation;
    
    private GId gId = mock(GId.class);
    
    public final Position position = new Position();
    public static DateTime dateTime;
    public final String crashReportWithLocation = "+RESP:GTCRA,260301,135790246811220,,00,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,20161111133140,11F0$";
    public final String crashReportWithoutLocation = "+RESP:GTCRA,260400,135790246811220,,02,0,,,,0,0,,0262,0001,194D,4C52,00,20161111133140,A615$";
    public final String crashBufferReportWithoutLocation = "+BUFF:GTCRA,260400,135790246811220,,02,0,,,,0,0,,0262,0001,194D,4C52,00,20161111133140,A615$";
    public QueclinkCrashReport queclinkCrashReport = new QueclinkCrashReport(trackerAgent);
    
    ArgumentCaptor<EventRepresentation> eventCaptor = ArgumentCaptor.forClass(EventRepresentation.class);
    @Before
    public void setup() {
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(trackerDevice);
        when(trackerDevice.getGId()).thenReturn(gId);
        
        position.setAlt(new BigDecimal("70.0"));
        position.setLng(new BigDecimal("121.354335"));
        position.setLat(new BigDecimal("31.222073"));
        
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        dateTime = formatter.parseDateTime("20161111133140");
        
        locationEventRepresentation = setupLocationEventRepresentation(gId);
    }
    
    public EventRepresentation setupLocationEventRepresentation (GId gId) {
        
        ManagedObjectRepresentation managedObject = new ManagedObjectRepresentation();
        managedObject.setId(gId);
        EventRepresentation locationUpdate = new EventRepresentation();
        locationUpdate.setType(TrackerDevice.LU_EVENT_TYPE);
        locationUpdate.setText("Crash detected");
        locationUpdate.setSource(managedObject);
        locationUpdate.setDateTime(dateTime);
        locationUpdate.set(position);
        
        return locationUpdate;
    }
    
    @Test
    public void testCrashReportAndEvents() {
        
        String[] crashReport = crashReportWithLocation.split(QUECLINK.getFieldSeparator());
        String imei = queclinkCrashReport.parse(crashReport);
        connectionDetails = new TestConnectionDetails();
        connectionDetails.setImei(imei);
        queclinkCrashReport.onParsed(new ReportContext(connectionDetails, crashReport));
        
        assertEquals("135790246811220", imei);
        verify(trackerAgent).getOrCreateTrackerDevice("135790246811220");
        verify(trackerDevice).crashDetectedEvent(position, dateTime);
        verify(trackerDevice).setPosition(eventCaptor.capture());
        Position actualPosition = eventCaptor.getValue().get(Position.class);
        String actualType = eventCaptor.getValue().getType();
        String actualText = eventCaptor.getValue().getText();
        DateTime actualDateTime = eventCaptor.getValue().getDateTime();
        assertEquals(locationEventRepresentation.get(Position.class), actualPosition);
        assertEquals(locationEventRepresentation.getType(), actualType);
        assertEquals(locationEventRepresentation.getText(), actualText);
        assertEquals(locationEventRepresentation.getDateTime(), actualDateTime);
        
        
        crashReport = crashReportWithoutLocation.split(QUECLINK.getFieldSeparator());
        connectionDetails = new TestConnectionDetails();
        connectionDetails.setImei(imei);
        queclinkCrashReport.onParsed(new ReportContext(connectionDetails, crashReport));
        
        verify(trackerAgent, times(2)).getOrCreateTrackerDevice("135790246811220");
        verify(trackerDevice).crashDetectedEvent(dateTime);
        verify(trackerDevice, times(1)).setPosition(eventCaptor.capture()); // no more interactions with this report
        
        crashReport = crashBufferReportWithoutLocation.split(QUECLINK.getFieldSeparator());
        connectionDetails = new TestConnectionDetails();
        connectionDetails.setImei(imei);
        queclinkCrashReport.onParsed(new ReportContext(connectionDetails, crashReport));
        
        verify(trackerAgent, times(3)).getOrCreateTrackerDevice("135790246811220");
        verify(trackerDevice, times(2)).crashDetectedEvent(dateTime);
        verify(trackerDevice, times(1)).setPosition(eventCaptor.capture()); // no more interactions with this report
        
    }
}
