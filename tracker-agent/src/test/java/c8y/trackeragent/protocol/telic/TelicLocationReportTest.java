package c8y.trackeragent.protocol.telic;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.cumulocity.rest.representation.event.EventRepresentation;

import c8y.Position;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.CommonConstants;
import c8y.trackeragent.protocol.telic.TelicConstants;
import c8y.trackeragent.protocol.telic.parser.FixType;
import c8y.trackeragent.protocol.telic.parser.LogCodeType;
import c8y.trackeragent.protocol.telic.parser.TelicLocationReport;
import c8y.trackeragent.service.MeasurementService;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;

public class TelicLocationReportTest {
    
    private TelicDeviceMessages deviceMessages = new TelicDeviceMessages();
    private MeasurementService measurementService = Mockito.mock(MeasurementService.class);
    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerDevice device = mock(TrackerDevice.class);
    private TelicLocationReport telic = new TelicLocationReport(trackerAgent, measurementService);
    private ArgumentCaptor<EventRepresentation> locationEventCaptor = ArgumentCaptor.forClass(EventRepresentation.class); 
    private ArgumentCaptor<Position> positionCaptor = ArgumentCaptor.forClass(Position.class); 

    @Before
    public void setup() throws Exception {
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);
        when(device.aLocationUpdateEvent()).thenReturn(new EventRepresentation());
    }
    
    private void verifyReport() {
        verify(trackerAgent).getOrCreateTrackerDevice(Devices.IMEI_1);
        verify(device).setPosition(locationEventCaptor.capture(), positionCaptor.capture());
    }
    
    @Test
    public void shouldParseImei() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        String actualImei = telic.parse(report);
        
        assertThat(actualImei).isEqualTo(Devices.IMEI_1);
    }
    
    @Test
    public void shouldUpdateDevicePosition() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verifyReport();
        assertThat(positionCaptor.getValue()).isEqualTo(Positions.SAMPLE_1);
    }
    
    @Test
    public void shouldSendLogCodeTypeInPositionFragment() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verifyReport();
        Object actualLogCodeType = positionCaptor.getValue().getProperty(CommonConstants.REPORT_REASON);
        assertThat(actualLogCodeType).isEqualTo(LogCodeType.TIME_EVENT.getLabel());
    }
    
    @Test
    public void shouldSendLogTimestampInPositionFragment() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verifyReport();
        String actualLogTimestamp = (String) positionCaptor.getValue().getProperty(TelicConstants.LOG_TIMESTAMP);
        assertThat(actualLogTimestamp).isEqualTo(TelicDeviceMessages.LOG_TIMESTAMP_C8Y_STR);
    }
    
    @Test
    public void shouldSendGPSTimestampInPositionFragment() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verifyReport();
        String actualLogTimestamp = (String) positionCaptor.getValue().getProperty(TelicConstants.LOG_TIMESTAMP);
        assertThat(actualLogTimestamp).isEqualTo(TelicDeviceMessages.LOG_TIMESTAMP_C8Y_STR);
    }
    
    @Test
    public void shouldSendFixTypeInPositionFragment() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verifyReport();
        Object actualFixType = positionCaptor.getValue().getProperty(TelicConstants.FIX_TYPE);
        assertThat(actualFixType).isEqualTo(FixType._3D.getLabel());
    }
    
    @Test
    public void shouldSendFixTypeInPositionFragmentEvenWhenItsNotStandard() throws Exception {
    	String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).set(TelicLocationReport.FIX_TYPE, "100").asArray();
    	
    	telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
    	
    	verifyReport();
    	Object actualFixType = positionCaptor.getValue().getProperty(TelicConstants.FIX_TYPE);
    	assertThat(actualFixType).isEqualTo("100");
    }
    
    @Test
    public void shouldSendSpeedAsMeasurement() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verify(measurementService).createSpeedMeasurement(new BigDecimal(4), device, TelicDeviceMessages.LOG_TIMESTAMP);
    }
   
    @Test
    public void shouldSendSatellitesInPositionFragment() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verifyReport();
        Object actualSatellites = positionCaptor.getValue().getProperty(TelicConstants.SATELLITES);
        assertThat(actualSatellites).isEqualTo(4);
    }
    
    @Test
    public void shouldSendAltitudeAsMeasurement() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verify(measurementService).createAltitudeMeasurement(Positions.SAMPLE_1.getAlt(), device, TelicDeviceMessages.LOG_TIMESTAMP);
    }
    
    @Test
    public void shouldSendMileageAsMeasurement() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verify(measurementService).createMileageMeasurement(new BigDecimal("11.032"), device, TelicDeviceMessages.LOG_TIMESTAMP);
    }
    
    @Test
    public void shouldSendBatteryAsMeasurement() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verify(measurementService).createBatteryLevelMeasurement(any(BigDecimal.class), eq(device), eq(TelicDeviceMessages.LOG_TIMESTAMP), eq("V"));
    }
    
    @Test
    public void shouldSendGeofenceEnterAsEvent() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1, LogCodeType.GEOFENCE_ENTER.getCode()).asArray();
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verify(device).geofenceEnter(TelicDeviceMessages.LOG_TIMESTAMP);
    }
    
    @Test
    public void shouldSendGeofenceExitAsEvent() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1, LogCodeType.GEOFENCE_EXIT.getCode()).asArray();
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verify(device).geofenceExit(TelicDeviceMessages.LOG_TIMESTAMP);
    }
    
    @Test
    public void shouldSendMotionStartAsEventAndMeasurement() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1, LogCodeType.MOTION_SENSOR_MOTION.getCode()).asArray();
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verify(device).motionEvent(true);
        verify(measurementService).createMotionMeasurement(true, device, TelicDeviceMessages.LOG_TIMESTAMP);
    }
    
    @Test
    public void shouldSendMotionStopAsEventAndMeasurement() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1, LogCodeType.MOTION_SENSOR_STATIONARY.getCode()).asArray();
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verify(device).motionEvent(false);
        verify(measurementService).createMotionMeasurement(false, device, TelicDeviceMessages.LOG_TIMESTAMP);
    }
    
    @Test
    public void shouldSendChargerConnectedAsEvent() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(report, Devices.IMEI_1, null));
        
        verify(device).chargerConnected(TelicDeviceMessages.LOG_TIMESTAMP);
    }
    
    @Test
    public void shouldSendChargerConnectedAsEventOnceOnly() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        ReportContext reportCtx = new ReportContext(report, Devices.IMEI_1, null);
        
        telic.onParsed(reportCtx);
        telic.onParsed(reportCtx);
        
        verify(device, Mockito.times(1)).chargerConnected(TelicDeviceMessages.LOG_TIMESTAMP);
    }
    
    @Test
	public void shouldNormalizeBatteryValue() throws Exception {
    	BigDecimal input = new BigDecimal(238);
    	
    	BigDecimal output = TelicLocationReport.normalizeBatteryLevel(input);
    	
    	assertThat(output).isEqualTo(new BigDecimal(4.22, TelicLocationReport.BATTERY_CALCULATION_MODE));
		
	}
    
}
