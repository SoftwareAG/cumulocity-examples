/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.coban.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import c8y.MotionTracking;
import c8y.Position;
import c8y.SpeedMeasurement;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;
import c8y.trackeragent.utils.message.TrackerMessage;

import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;

public class PositionUpdateCobanParserTest extends CobanParserTestSupport {

    private PositionUpdateCobanParser cobanParser;

    private ArgumentCaptor<EventRepresentation> eventCaptor;

    private ArgumentCaptor<BigDecimal> speedCaptor;

    private ArgumentCaptor<CobanAlarmType> alarmTypeCaptor;

    @Before
    public void init() {
        cobanParser = new PositionUpdateCobanParser(trackerAgent, serverMessages, alarmService, measurementService);
        alarmTypeCaptor = ArgumentCaptor.forClass(CobanAlarmType.class);
        eventCaptor = ArgumentCaptor.forClass(EventRepresentation.class);
        speedCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        when(measurementService.createSpeedMeasurement(any(BigDecimal.class), any(TrackerDevice.class))).thenAnswer(new CreateSpeedMeasurementAnswer());
        connectionDetails.setImei("ABCD");
    }

    @Test
    public void shouldAcceptCobanPositionMessage() throws Exception {
        boolean actual = cobanParser.accept(deviceMessages.positionUpdate("ABCD", Positions.ZERO).asArray());

        assertThat(actual).isTrue();

    }

    @Test
    public void shouldParseImei() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.positionUpdate("ABCD", Positions.ZERO);
        String actual = cobanParser.parse(deviceMessage.asArray());

        assertThat(actual).isEqualTo("ABCD");
    }

    @Test
    public void shouldProcessPositionUpdate() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.positionUpdate("ABCD", Positions.TK10xSample);
        when(trackerAgent.getOrCreateTrackerDevice("ABCD")).thenReturn(deviceMock);
        ReportContext reportCtx = new ReportContext(connectionDetails, deviceMessage.asArray());

        boolean success = cobanParser.onParsed(reportCtx);

        verify(deviceMock).setPosition(eventCaptor.capture());
        assertThat(success).isTrue();
        assertThat(eventCaptor.getValue().get(SpeedMeasurement.class)).isNotNull();
        Position position = eventCaptor.getValue().get(Position.class);
        assertThat(position).isEqualTo(TK10xCoordinatesTranslator.parse(Positions.TK10xSample));
    }

    @Test
    public void shouldTranslateOperation() throws Exception {
        MotionTracking motionTracking = new MotionTracking();
        motionTracking.setActive(true);
        motionTracking.setProperty("cobanRequest", "101,30m");
        OperationRepresentation operation = new OperationRepresentation();
        operation.set(motionTracking);
        OperationContext operationCtx = new OperationContext(connectionDetails, operation);

        String msg = cobanParser.translate(operationCtx);

        assertThat(operation.get(CobanSupport.OPERATION_FRAGMENT_SERVER_COMMAND)).isEqualTo("**,imei:ABCD,101,30m;");
        assertThat(msg).isEqualTo("**,imei:ABCD,101,30m;");
    }

    @Test
    public void shouldSendAlarmIfNoGpsSignal() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.positionUpdateNoGPS("ABCD");
        ReportContext reportCtx = new ReportContext(connectionDetails, deviceMessage.asArray());

        boolean success = cobanParser.onParsed(reportCtx);

        verify(deviceMock, never()).setPosition(Mockito.any(Position.class));
        assertThat(success).isTrue();
        verify(alarmService).createAlarm(any(ReportContext.class), alarmTypeCaptor.capture(), any(TrackerDevice.class));
        assertThat(alarmTypeCaptor.getValue()).isEqualTo(CobanAlarmType.NO_GPS_SIGNAL);
    }

    @Test
    public void shouldAcceptAlarms() throws Exception {
        for (CobanAlarmType alarmType : CobanAlarmType.values()) {
            TrackerMessage msg = deviceMessages.alarm("ABCD", alarmType);

            assertThat(cobanParser.accept(msg.asArray())).isTrue();
        }
    }

    @Test
    public void shouldCreateLowBatteryAlarm() throws Exception {
        cobanParser.onParsed(anAlarmReport(CobanAlarmType.LOW_BATTERY));

        verify(alarmService).createAlarm(any(ReportContext.class), alarmTypeCaptor.capture(), any(TrackerDevice.class));
        CobanAlarmType cobanAlarmType = alarmTypeCaptor.getValue();
        assertThat(cobanAlarmType).isEqualTo(CobanAlarmType.LOW_BATTERY);
    }

    @Test
    public void shouldCreateSpeedMeasurement() throws Exception {
        TrackerMessage report = deviceMessages.positionUpdate("ABCD", 154);
        ReportContext reportCtx = new ReportContext(connectionDetails, report.asArray());

        cobanParser.onParsed(reportCtx);

        verify(measurementService).createSpeedMeasurement(speedCaptor.capture(), any(TrackerDevice.class));
        assertThat(speedCaptor.getValue())
                .isEqualTo((new BigDecimal(154)).multiply(CobanParser.COBAN_SPEED_MEASUREMENT_FACTOR).setScale(0, BigDecimal.ROUND_DOWN));

    }

    private ReportContext anAlarmReport(CobanAlarmType alarmType) {
        String[] report = deviceMessages.alarm("ABCD", alarmType).asArray();
        return new ReportContext(connectionDetails, report);
    }

}
