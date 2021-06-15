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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;

import c8y.Position;
import c8y.trackeragent.configuration.TrackerConfiguration;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.RFV16ParserTestSupport;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;
import c8y.trackeragent.utils.message.TrackerMessage;

public class PositionUpdateRFV16ParserTest extends RFV16ParserTestSupport {
    
    PositionUpdateRFV16Parser parser;
    ArgumentCaptor<Position> positionCaptor = ArgumentCaptor.forClass(Position.class);
    ArgumentCaptor<EventRepresentation> eventCaptor = ArgumentCaptor.forClass(EventRepresentation.class);
    ArgumentCaptor<RFV16AlarmType> alarmTypeCaptor = ArgumentCaptor.forClass(RFV16AlarmType.class);
    ArgumentCaptor<MeasurementRepresentation> measurementCaptor = ArgumentCaptor.forClass(MeasurementRepresentation.class);
    TrackerConfiguration trackerConfiguration = new TrackerConfiguration();
    
    @Before
    public void init() {
        parser = new PositionUpdateRFV16Parser(trackerAgent, serverMessages, measurementService, alarmService, trackerConfiguration);
        trackerConfiguration.setRfv16LocationReportTimeInterval(30);
        when(alarmService.createAlarm(any(ReportContext.class), any(RFV16AlarmType.class), any(TrackerDevice.class))).thenAnswer(new CreateAlarmAnswer());
    }
    
    @Test    
    public void shouldParseImeiFromPositionMessage() throws Exception {
        String imei = parser.parse(deviceMessages.positionUpdate("DB", IMEI,Positions.TK10xSample).asArray());
        
        assertThat(imei).isEqualTo(IMEI);
    }
    
    @Test
    public void shouldProcessPositionUpdateV1() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.positionUpdate("DB", IMEI, Positions.TK10xSample);
        ReportContext reportCtx = new ReportContext(connectionDetails, deviceMessage.asArray());
        
        parser.onParsed(reportCtx);
        
        verify(deviceMock).setPosition(eventCaptor.capture());
        Position position = eventCaptor.getValue().get(Position.class);
        assertThat(position).isEqualTo(TK10xCoordinatesTranslator.parse(Positions.TK10xSample));
        assertOut("*HQ,1234567890,D1,010000,30,1#");
    }
    
    /**
     * The value is not use yet
     */
    @Test
    public void shouldParseDateTime() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.positionUpdate("DB", IMEI, Positions.TK10xSample);
        ReportContext reportCtx = new ReportContext(connectionDetails, deviceMessage.asArray());
        DateTime dateTime = RFV16Parser.getDateTime(reportCtx);
        
        assertThat(dateTime).isEqualTo(SOME_DATE_TIME);
    }
    
    @Test
    public void shouldCreateAlarmIfPresent() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.positionUpdate("DB", IMEI, Positions.TK10xSample, "FFFDFFFF");
        ReportContext reportCtx = new ReportContext(connectionDetails, deviceMessage.asArray());
        
        parser.onParsed(reportCtx);
        
        verify(alarmService).createAlarm(any(ReportContext.class), alarmTypeCaptor.capture(), any(TrackerDevice.class));
        assertThat(alarmTypeCaptor.getValue()).isEqualTo(RFV16AlarmType.LOW_BATTERY);
    }
    
    @Test
    public void shouldNotCreateAlarmIfNotPresent() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.positionUpdate("DB", IMEI, Positions.TK10xSample, "FFFFFFFF");
        ReportContext reportCtx = new ReportContext(connectionDetails, deviceMessage.asArray());
        
        parser.onParsed(reportCtx);
        
        verify(alarmService, never()).createAlarm(any(ReportContext.class), any(RFV16AlarmType.class), any(TrackerDevice.class));
    }
    
    @Test
    public void shouldNotSentControlCommandsIfAlreadySentForTheConnection() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.positionUpdate("DB", IMEI, Positions.TK10xSample);
        ReportContext reportCtx = new ReportContext(connectionDetails, deviceMessage.asArray());
        reportCtx.setConnectionParam(RFV16Constants.CONNECTION_PARAM_CONTROL_COMMANDS_SENT, true);
        
        parser.onParsed(reportCtx);
        
        assertNothingOut();
    }
    
    @Test
    public void shouldCreateSpeedMeasurement() throws Exception {
        ArgumentCaptor<BigDecimal> speedCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        TrackerMessage deviceMessage = deviceMessages.positionUpdateWithSpeed("DB", IMEI, 60);
        ReportContext reportCtx = new ReportContext(connectionDetails, deviceMessage.asArray());
        
        parser.onParsed(reportCtx);
        
        verify(measurementService).createSpeedMeasurement(speedCaptor.capture(), any(TrackerDevice.class));
        assertThat(speedCaptor.getAllValues().size()).isEqualTo(1);
        assertThat(speedCaptor.getValue()).isEqualTo((new BigDecimal(60)).multiply(RFV16Parser.RFV16_SPEED_MEASUREMENT_FACTOR).setScale(0, BigDecimal.ROUND_DOWN));
    }
    
    @Test
    public void shouldSendAlarmsEitherIfCurrentEventHasNoGpsData() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.invalidPositionUpdate("DB", IMEI, "FFFDFFFF");
        ReportContext reportCtx = new ReportContext(connectionDetails, deviceMessage.asArray());
        
        parser.onParsed(reportCtx);
        
        verify(alarmService).createAlarm(any(ReportContext.class), alarmTypeCaptor.capture(), any(TrackerDevice.class));
        assertThat(alarmTypeCaptor.getValue()).isEqualTo(RFV16AlarmType.LOW_BATTERY);
    }
    
    @Test
    public void shouldPutAlarmsToLastEventIfCurrentEventHasNoGpsData() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.invalidPositionUpdate("DB", IMEI, "FFFDFFFF");
        ReportContext reportCtx = new ReportContext(connectionDetails, deviceMessage.asArray());
        when(deviceMock.getLastPosition()).thenReturn(Positions.SAMPLE_1);
        
        parser.onParsed(reportCtx);
        
        verify(deviceMock).setPosition(eventCaptor.capture());
        assertThat(eventCaptor.getValue().get(Position.class)).isEqualTo(Positions.SAMPLE_1);
    }

}
