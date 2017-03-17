package c8y.trackeragent.protocol.queclink;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;

import static org.mockito.Matchers.anyString;
import static c8y.trackeragent.protocol.TrackingProtocol.QUECLINK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static org.mockito.Mockito.times;

import c8y.MotionTracking;
import c8y.Tracking;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.device.BaseQueclinkDevice;
import c8y.trackeragent.protocol.queclink.device.GL300;
import c8y.trackeragent.protocol.queclink.device.GL505;
import c8y.trackeragent.protocol.queclink.device.QueclinkDevice;
import c8y.trackeragent.protocol.queclink.parser.QueclinkDeviceMotionState;
import c8y.trackeragent.protocol.queclink.parser.QueclinkIgnition;
import c8y.trackeragent.server.TestConnectionDetails;
import c8y.trackeragent.utils.QueclinkReports;

public class QueclinkDeviceMotionStateTest {

    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerDevice trackerDevice = mock(TrackerDevice.class);
    private QueclinkDevice queclinkDevice;
    private TestConnectionDetails connectionDetails = new TestConnectionDetails();
    private OperationRepresentation operation = new OperationRepresentation();
    ManagedObjectRepresentation managedObject;
    
    private MotionTracking motionTracking = new MotionTracking();
    private Tracking tracking = new Tracking();
    private BaseQueclinkDevice gl505 = new GL505();
    private BaseQueclinkDevice gl300 = new GL300();
    
    private QueclinkIgnition ignition = mock(QueclinkIgnition.class);
    private QueclinkDeviceMotionState queclinkDeviceMotionState = new QueclinkDeviceMotionState(trackerAgent, ignition);
    
    public static final String[] MOTION_SETTING = {
            // report interval on motion, set motion active, reboot
            "AT+GTFRI=gl300,1,,,,,,300,300,,,,,,,,,,,,0002$AT+GTCFG=gl300,,,,,,,,,,303,1,,,,,,,,,,0002$", // specific to gl200, gl300, gv500
            // only enable motion sensor, reboot
            "AT+GTCFG=gl300,,,,,,,,,,303,1,,,,,,,,,,0002$", // specific to gl200, gl300, gv500
            // only disable motion sensor, reboot
            "AT+GTCFG=gl300,,,,,,,,,,47,0,,,,,,,,,,0002$", // specific to gl200, gl300, gv500
            // report interval on motion and set motion active, reboot
            "AT+GTGBC=gl500,,,,,,,,,,,,,5,,1,,,,,,,,0002$", // specific to gl50x
            // only enable motion sensor, reboot
            "AT+GTGBC=gl500,,,,,,,,,,,,,,,1,,,,,,,,0002$", // specific to gl50x
            // only disable motion sensor, reboot
            "AT+GTGBC=gl500,,,,,,,,,,,,,,,0,,,,,,,,0002$", // specific to gl50x
            // report interval on motion, set motion active, reboot
            "AT+GTFRI=gl300,1,,,,,,300,300,,,,,,,,,,,,0002$AT+GTCFG=gl300,,,,,,,,,,47,0,,,,,,,,,,0002$", // specific to gl200, gl300, gv500
            // report interval on motion and set motion active, reboot
            "AT+GTGBC=gl500,,,,,,,,,,,,,5,,0,,,,,,,,0002$" // specific to gl50x           
    };
    
    public static final String[] ACK_MOTION_SETTING = {
            // report interval on motion, set motion active, reboot
            "+ACK:GTCFG,300400,860599001073709,,0002,20161004134929,27FB$", // specific to gl200, gl300, gv500
            // report interval on motion and set motion active, reboot
            "+ACK:GTGBC,110302,868487003422904,GL500,0002,20161007154349,3304$" // specific to gl50x
    };
    
    /**
     * Commands to device
     */
    public final String[] nonMovementReportInterval = {
        "AT+GTNMD=gl300,E,,,,300,300,,,,,,,,0002$", //specific to gl200, gl300
        "AT+GTNMD=gl500,E,,,,5,,,,0002$", // specific to gl50x
        "AT+GTFRI=gl300,1,,,,,,300,300,,,,,,,,,,,,0002$AT+GTCFG=gl300,,,,,,,,,,47,0,,,,,,,,,,0002$AT+GTNMD=gl300,E,,,,300,300,,,,,,,,0002$", //specific to gl200, gl300 - case of motion sensor disabled
        "AT+GTGBC=gl500,,,,,,,,,,,,,5,,0,,,,,,,,0002$AT+GTNMD=gl500,E,,,,5,,,,0002$" // specific to gl50x, case of motion sensor disabled
    };
    
    /**
     * Acknowledgement from device
     */
    public final String[] ackNonMovementReportInterval = {
            "+ACK:GTNMD,300400,860599001073709,,0002,20161004134115,27EF$", //specific to gl200, gl300, together with reboot command
            "+ACK:GTNMD,110302,868487003422904,GL500,0002,20161007155444,330D$", // specific to gl50x, together with reboot command
    };
    
    @Before
    public void setup() {
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(trackerDevice);
    }
    
    public void translate_setup() {
        queclinkDeviceMotionState = spy(new QueclinkDeviceMotionState(trackerAgent, ignition));
        queclinkDevice = mock(QueclinkDevice.class);
        
        when(queclinkDeviceMotionState.getQueclinkDevice()).thenReturn(queclinkDevice);
        when(queclinkDevice.getDeviceByType("queclink_gl505")).thenReturn(gl505);
        when(queclinkDevice.getDeviceByType("queclink_gl300")).thenReturn(gl300);
    }
    
    public void setMotionTrackingOptions(boolean active, int interval) {
        motionTracking.setActive(active);
        motionTracking.setInterval(interval);
    }
    
    @Test
    public void testDeviceMotionCommand() {
        
        setMotionTrackingOptions(true, 300); //motion tracking active, interval is 300 seconds
        
        assertEquals(MOTION_SETTING[0], translateReportIntervalOperation("gl300", "gl300"));
        assertEquals(MOTION_SETTING[3], translateReportIntervalOperation("gl505", "gl500"));
        
        setMotionTrackingOptions(true, 0); //enable motion sensor
        
        assertEquals(MOTION_SETTING[1], translateReportIntervalOperation("gl300", "gl300"));
        assertEquals(MOTION_SETTING[4], translateReportIntervalOperation("gl505", "gl500"));
        
        setMotionTrackingOptions(false, 0); //disable motion sensor
        
        assertEquals(MOTION_SETTING[2], translateReportIntervalOperation("gl300", "gl300"));
        assertEquals(MOTION_SETTING[5], translateReportIntervalOperation("gl505", "gl500"));
        
        setMotionTrackingOptions(false, 200); //disable motion sensor, set report interval
        tracking.setInterval(300);
        
        assertEquals(MOTION_SETTING[6], translateReportIntervalOperation("gl300", "gl300"));
        assertEquals(MOTION_SETTING[7], translateReportIntervalOperation("gl505", "gl500"));
        
    }
    
    @Test
    public void testAckDeviceMotionCommand() {
         
        // gl300
        setMotionTrackingOptions(true, 300);
        translateReportIntervalOperation("gl300", "gl300");

        ReportContext reportCtx = generateReportContext(ACK_MOTION_SETTING[0]);
        queclinkDeviceMotionState.onParsed(reportCtx);
        
        verify(trackerAgent).getOrCreateTrackerDevice(reportCtx.getImei());
        verify(trackerDevice).setMotionTracking(true, 300);
        
        setMotionTrackingOptions(false, 200);
        tracking.setInterval(300);
        translateReportIntervalOperation("gl300", "gl300");
        
        reportCtx = generateReportContext(ACK_MOTION_SETTING[0]);
        queclinkDeviceMotionState.onParsed(reportCtx);
        
        verify(trackerAgent, times(2)).getOrCreateTrackerDevice(reportCtx.getImei());
        verify(trackerDevice).setMotionTracking(false, 300);
        
        // gl500
        setMotionTrackingOptions(true, 300);
        translateReportIntervalOperation("gl505", "gl500");
        reportCtx = generateReportContext(ACK_MOTION_SETTING[1]);
        queclinkDeviceMotionState.onParsed(reportCtx);
        
        verify(trackerAgent).getOrCreateTrackerDevice(reportCtx.getImei());
        verify(trackerDevice, times(2)).setMotionTracking(true, 300);
        
        setMotionTrackingOptions(false, 200);
        tracking.setInterval(300);
        translateReportIntervalOperation("gl505", "gl500");
        reportCtx = generateReportContext(ACK_MOTION_SETTING[1]);
        queclinkDeviceMotionState.onParsed(reportCtx);
        
        verify(trackerAgent, times(2)).getOrCreateTrackerDevice(reportCtx.getImei());
        verify(trackerDevice, times(2)).setMotionTracking(false, 300);
        
    }
    
    public ReportContext generateReportContext(String report) {
        String[] reportArr = report.split(QUECLINK.getFieldSeparator());
        String imei = queclinkDeviceMotionState.parse(reportArr);
        
        connectionDetails  = new TestConnectionDetails();
        connectionDetails.setImei(imei);
        
        return new ReportContext(connectionDetails, reportArr);
    }
    
    public String translateReportIntervalOperation (String deviceType, String password) {
        
        translate_setup();
        
        GId device_gid = new GId("0");
        
        operation.set(motionTracking);
        operation.setDeviceId(device_gid);
        OperationContext operationCtx;
        connectionDetails = new TestConnectionDetails();
        operationCtx = new OperationContext(connectionDetails, operation);
        
        // prepare managed object
        managedObject = new ManagedObjectRepresentation();
        managedObject.setType("queclink_" + deviceType);
        managedObject.set(tracking);
        when(queclinkDevice.getManagedObjectFromGId(any(GId.class))).thenReturn(managedObject);
        
        String deviceCommand = queclinkDeviceMotionState.translate(operationCtx);
        
        verify(queclinkDevice).getManagedObjectFromGId(device_gid);
        
        return deviceCommand; 
    }
    
    
    @Test
    public void testNonMovementReportInterval() {
        
        tracking.setInterval(300);
        
        String translatedOperation;
               
        motionTracking.setActive(true);
        translatedOperation = translateNonMovementReportIntervalOperation("gl300", "gl300");      
        assertEquals(nonMovementReportInterval[0], translatedOperation);
        
        translatedOperation = translateNonMovementReportIntervalOperation("gl505", "gl500");      
        assertEquals(nonMovementReportInterval[1], translatedOperation);
        
        motionTracking.setActive(false);
        translatedOperation = translateNonMovementReportIntervalOperation("gl300", "gl300");      
        assertEquals(nonMovementReportInterval[2], translatedOperation);
        
        translatedOperation = translateNonMovementReportIntervalOperation("gl505", "gl500");      
        assertEquals(nonMovementReportInterval[3], translatedOperation);
        
    }
    
    public String translateNonMovementReportIntervalOperation (String deviceType, String password) {
        translate_setup();
        
        GId device_gid = new GId("0");
        
        OperationContext operationCtx;
        OperationRepresentation operation = new OperationRepresentation();

        operation.set(tracking);
        operation.setDeviceId(device_gid);
        
        connectionDetails = new TestConnectionDetails();
        operationCtx = new OperationContext(connectionDetails, operation);
        
        managedObject = new ManagedObjectRepresentation();
        managedObject.setType("queclink_" + deviceType);
        managedObject.set(motionTracking);
        when(queclinkDevice.getManagedObjectFromGId(any(GId.class))).thenReturn(managedObject);
        
        String deviceCommand = queclinkDeviceMotionState.translate(operationCtx);
        
        verify(queclinkDevice).getManagedObjectFromGId(device_gid);
        
        return deviceCommand; 
    }
    
    @Test
    public void testAckNonMovementReportInterval() {
        tracking.setInterval(300);
        
        // gl300
        translateNonMovementReportIntervalOperation("gl300", "gl300");
        ReportContext reportCtx = generateReportContext(ackNonMovementReportInterval[0]);
        queclinkDeviceMotionState.onParsed(reportCtx);
        verify(trackerAgent).getOrCreateTrackerDevice(reportCtx.getImei());
        verify(trackerDevice).setTracking(300);
        
        // gl505
        translateNonMovementReportIntervalOperation("gl505", "gl500");
        reportCtx = generateReportContext(ackNonMovementReportInterval[1]);
        queclinkDeviceMotionState.onParsed(reportCtx);
        verify(trackerAgent).getOrCreateTrackerDevice(reportCtx.getImei());
        verify(trackerDevice, times(2)).setTracking(300);
    }
    
    @Test
    public void shouldCreateTowEvent() {
        String[] towReports = {
                "+RESP:GTSTT,3C0101,135790246811220,,16,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,20090214093254,11F0$", //gv75 report
                "+RESP:GTSTT,1A0500,135790246811220,,16,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,20100214093254,11F0$" //gl300 report
        };
        
        for (String towReport : towReports) {
            queclinkDeviceMotionState.onParsed(new ReportContext(connectionDetails, 
                    towReport.split(QUECLINK.getFieldSeparator())));
        }
        
        DateTime expectedTime = QueclinkReports.convertEntryToDateTime("20090214093254");
        verify(trackerDevice).towEvent(expectedTime);
    }
    
    @Test
    public void shouldNotCreateTowEvent() {
        String[] towReports = {
                "+RESP:GTSTT,3C0101,135790246811220,,12,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,20090214093254,11F0$", //gv75 report
                "+RESP:GTSTT,1A0500,135790246811220,,22,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,20100214093254,11F0$", //gl300 report
                "+RESP:GTNMR,300400,860599001073709,,0,1,1,1,0.0,28,43.3,24.950411,60.193572,20161005072235,0244,0091,0D9F,ABEE,,96,20161005072236,2CC0$"
        };
        
        for (String towReport : towReports) {
            queclinkDeviceMotionState.onParsed(new ReportContext(connectionDetails, 
                    towReport.split(QUECLINK.getFieldSeparator())));
        }
        
        verify(trackerDevice, never()).towEvent((DateTime) any());
    }
    
    @Test
    public void shouldCreateIgnitionEvent() {
        String ignitionOnReport = "+RESP:GTSTT,1A0500,135790246811220,,22,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,20100214093254,11F0$";
        connectionDetails.setImei("135790246811220");
        ReportContext reportCtx = new ReportContext(connectionDetails, 
                ignitionOnReport.split(QUECLINK.getFieldSeparator()));
        
        queclinkDeviceMotionState.onParsed(reportCtx);
        
        verify(ignition).createIgnitionOnEvent(reportCtx, "135790246811220");
        verify(ignition, never()).createIgnitionOffEvent(reportCtx, "135790246811220");
        
        String ignitionOffReport = "+RESP:GTSTT,1A0500,135790246811220,,12,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,20100214093254,11F0$";
        
        reportCtx = new ReportContext(connectionDetails, 
                ignitionOffReport.split(QUECLINK.getFieldSeparator()));
        queclinkDeviceMotionState.onParsed(reportCtx);
        
        verify(ignition).createIgnitionOffEvent(reportCtx, "135790246811220");
        verify(ignition, times(1)).createIgnitionOnEvent(reportCtx, "135790246811220");
        
    }
}
