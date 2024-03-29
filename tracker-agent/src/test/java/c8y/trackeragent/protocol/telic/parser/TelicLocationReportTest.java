/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.telic.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.joda.time.DateTime;
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
import c8y.trackeragent.protocol.telic.TelicDeviceMessages;
import c8y.trackeragent.server.TestConnectionDetails;
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
    private TestConnectionDetails connectionDetails = new TestConnectionDetails(Devices.IMEI_1);

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
        
        telic.onParsed(new ReportContext(connectionDetails, report));
        
        verifyReport();
        assertThat(positionCaptor.getValue()).isEqualTo(Positions.SAMPLE_1);
    }
    
    @Test
    public void shouldUpdateDeviceGpsAccuracy() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_5).asArray();

        telic.onParsed(new ReportContext(connectionDetails, report));

        verifyReport();
        assertThat(positionCaptor.getValue()).isEqualTo(Positions.SAMPLE_5);
        assertThat(positionCaptor.getValue().getAccuracy()).isEqualTo(Positions.SAMPLE_5.getAccuracy());
    }

    @Test
    public void shouldNotUpdateDeviceGpsAccuracy() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();

        telic.onParsed(new ReportContext(connectionDetails, report));

        verifyReport();
        assertThat(positionCaptor.getValue()).isEqualTo(Positions.SAMPLE_1);
        assertThat(positionCaptor.getValue().getAccuracy()).isNull();
    }

    @Test
    public void shouldUpdateDeviceGpsAccuracyFromRaw1() throws Exception {
        String msg1 = "0020xxxxxx99,100816221715,0,100816221715,008796214,53057035,6,0,0,0,30,0,0,54945,26201,0010,00,154,0,0,0109,4533,69";
        String[] report1 = msg1.split(",");
        telic.onParsed(new ReportContext(connectionDetails, report1));
        verifyReport();
        assertThat(positionCaptor.getValue().getAccuracy()).isEqualTo(69);
    }

    @Test
    public void shouldUpdateDeviceGpsAccuracyFromRaw2() throws Exception {
        String msg2 = "0020xxxxxx99,100816221727,0,100816221727,008795539,53056970,6,0,0,0,30,0,0,54976,26201,0010,00,143,0,0,0109,14263,119";
        String[] report2 = msg2.split(",");
        telic.onParsed(new ReportContext(connectionDetails, report2));
        verifyReport();
        assertThat(positionCaptor.getValue().getAccuracy()).isEqualTo(119);
    }

    @Test
    public void shouldSendLogCodeTypeInPositionFragment() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(connectionDetails, report));
        
        verifyReport();
        Object actualLogCodeType = positionCaptor.getValue().getProperty(CommonConstants.REPORT_REASON);
        assertThat(actualLogCodeType).isEqualTo(LogCodeType.TIME_EVENT.getLabel());
    }
    
    @Test
    public void shouldSendLogTimestampInPositionFragment() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(connectionDetails, report));
        
        verifyReport();
        String actualLogTimestamp = (String) positionCaptor.getValue().getProperty(TelicConstants.LOG_TIMESTAMP);
        assertThat(actualLogTimestamp).isEqualTo(TelicDeviceMessages.LOG_TIMESTAMP_C8Y_STR);
    }
    
    @Test
    public void shouldSendGPSTimestampInPositionFragment() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(connectionDetails, report));
        
        verifyReport();
        String actualLogTimestamp = (String) positionCaptor.getValue().getProperty(TelicConstants.LOG_TIMESTAMP);
        assertThat(actualLogTimestamp).isEqualTo(TelicDeviceMessages.LOG_TIMESTAMP_C8Y_STR);
    }
    
    @Test
    public void shouldSendFixTypeInPositionFragment() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(connectionDetails, report));
        
        verifyReport();
        Object actualFixType = positionCaptor.getValue().getProperty(TelicConstants.FIX_TYPE);
        assertThat(actualFixType).isEqualTo(FixType._3D.getLabel());
    }
    
    @Test
    public void shouldSendFixTypeInPositionFragmentEvenWhenItsNotStandard() throws Exception {
    	String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).set(TelicLocationReport.FIX_TYPE, "100").asArray();
    	
    	telic.onParsed(new ReportContext(connectionDetails, report));
    	
    	verifyReport();
    	Object actualFixType = positionCaptor.getValue().getProperty(TelicConstants.FIX_TYPE);
    	assertThat(actualFixType).isEqualTo("100");
    }
    
    @Test
    public void shouldSendSpeedAsMeasurement() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(connectionDetails, report));
        
        verify(measurementService).createSpeedMeasurement(new BigDecimal(4), device, TelicDeviceMessages.LOG_TIMESTAMP);
    }
   
    @Test
    public void shouldSendSatellitesInPositionFragment() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(connectionDetails, report));
        
        verifyReport();
        Object actualSatellites = positionCaptor.getValue().getProperty(TelicConstants.SATELLITES);
        assertThat(actualSatellites).isEqualTo(4);
    }
    
    @Test
    public void shouldSendAltitudeAsMeasurement() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(connectionDetails, report));
        
        verify(measurementService).createAltitudeMeasurement(Positions.SAMPLE_1.getAlt(), device, TelicDeviceMessages.LOG_TIMESTAMP);
    }
    
    @Test
    public void shouldSendMileageAsMeasurement() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(connectionDetails, report));
        
        verify(measurementService).createMileageMeasurement(new BigDecimal("11.032"), device, TelicDeviceMessages.LOG_TIMESTAMP);
    }
    
    @Test
    public void shouldSendBatteryAsMeasurement() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(connectionDetails, report));
        
        verify(measurementService).createBatteryLevelMeasurement(any(BigDecimal.class), eq(device), eq(TelicDeviceMessages.LOG_TIMESTAMP), eq("V"));
    }
    
    @Test
    public void shouldSendGeofenceEnterAsEvent() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1, LogCodeType.GEOFENCE_ENTER.getCode()).asArray();
        
        telic.onParsed(new ReportContext(connectionDetails, report));
        
        verify(device).geofenceEnter(TelicDeviceMessages.LOG_TIMESTAMP);
    }
    
    @Test
    public void shouldSendGeofenceExitAsEvent() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1, LogCodeType.GEOFENCE_EXIT.getCode()).asArray();
        
        telic.onParsed(new ReportContext(connectionDetails, report));
        
        verify(device).geofenceExit(TelicDeviceMessages.LOG_TIMESTAMP);
    }
    
    @Test
    public void shouldSendMotionStartAsEventAndMeasurement() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1, LogCodeType.MOTION_SENSOR_MOTION.getCode()).asArray();
        
        telic.onParsed(new ReportContext(connectionDetails, report));
        
        verify(device).motionEvent(eq(true), any(DateTime.class));
        verify(measurementService).createMotionMeasurement(true, device, TelicDeviceMessages.LOG_TIMESTAMP);
    }
    
    @Test
    public void shouldSendMotionStopAsEventAndMeasurement() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1, LogCodeType.MOTION_SENSOR_STATIONARY.getCode()).asArray();
        
        telic.onParsed(new ReportContext(connectionDetails, report));
        
        verify(device).motionEvent(eq(false), any(DateTime.class));
        verify(measurementService).createMotionMeasurement(false, device, TelicDeviceMessages.LOG_TIMESTAMP);
    }
    
    @Test
    public void shouldSendChargerConnectedAsEvent() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        
        telic.onParsed(new ReportContext(connectionDetails, report));
        
        verify(device).chargerConnected(TelicDeviceMessages.LOG_TIMESTAMP);
    }
    
    @Test
    public void shouldSendChargerConnectedAsEventOnceOnly() throws Exception {
        String[] report = deviceMessages.positionUpdate(Devices.IMEI_1, Positions.SAMPLE_1).asArray();
        ReportContext reportCtx = new ReportContext(connectionDetails, report);
        
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
    
    @Test
    public void shouldNormalizeBatteryOnlyForCertainDevice() throws Exception {
        String battery = "238";
        String[] report = new String[21];
        report[20] = TelicLocationReport.devicesRequireBatteryCalculation.get(0);
        report[17] = battery;
        BigDecimal batteryLevel = telic.getBatteryLevel(new ReportContext(connectionDetails, report));
        
        assertThat(batteryLevel).isEqualTo(new BigDecimal(4.22, TelicLocationReport.BATTERY_CALCULATION_MODE));
    }
    
    @Test
    public void shouldNotNormalizeBattery() throws Exception {
        String deviceId = "0209";
        String battery = "4220";
        String[] report = new String[21];
        report[20] = deviceId;
        report[17] = battery;
        BigDecimal batteryLevel = telic.getBatteryLevel(new ReportContext(connectionDetails, report));
        
        assertThat(batteryLevel).isEqualTo(new BigDecimal(4.22, TelicLocationReport.BATTERY_CALCULATION_MODE));
    }
    
}
