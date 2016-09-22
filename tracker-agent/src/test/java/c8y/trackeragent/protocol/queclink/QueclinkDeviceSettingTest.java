package c8y.trackeragent.protocol.queclink;

import c8y.Hardware;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.device.QueclinkDevice;
import c8y.trackeragent.protocol.queclink.parser.QueclinkDeviceSetting;
import c8y.trackeragent.server.TestConnectionDetails;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import static c8y.trackeragent.protocol.TrackingProtocol.QUECLINK;

public class QueclinkDeviceSettingTest {

    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerDevice trackerDevice = mock(TrackerDevice.class);
    private ManagedObjectRepresentation managedObject = mock(ManagedObjectRepresentation.class);
    private String MO_type;
    private TestConnectionDetails connectionDetails = new TestConnectionDetails();
    
    public final String IMEI = "860599001073709";
    public final String type = "queclink_30";
    public final Hardware queclinkHardware = new Hardware("Queclink", IMEI, "04.00");
    public final String queclinkDataStr = "+RESP:GTFRI,300400,860599001073709,,0,0,1,0,0.0,215,1.9,24.950449,60.193629,20160919101701,0244,0091,0D9F,ABEE,,95,20160921072832,F510$";
    public final String[] queclinkData = queclinkDataStr.split(QUECLINK.getFieldSeparator()); 
    
    public QueclinkDeviceSetting queclinkDeviceSetting = new QueclinkDeviceSetting(trackerAgent);
    
    @Before
    public void setup() {
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(trackerDevice);
        when(trackerDevice.getManagedObject()).thenReturn(managedObject);
        when(managedObject.getType()).thenReturn(MO_type);
        connectionDetails.setImei(IMEI);
    }
    
    @Test
    public void testTypeAfterParse() {
        
        trackerAgent.getOrCreateTrackerDevice(IMEI);
        
        queclinkDeviceSetting.onParsed(new ReportContext(connectionDetails, queclinkData));
        //assertEquals(MO_type, type);
        
    }
    
    @Test
    public void testHardwareAfterParse() {
        
        queclinkDeviceSetting.onParsed(new ReportContext(connectionDetails, queclinkData));
        
        
    }
}
