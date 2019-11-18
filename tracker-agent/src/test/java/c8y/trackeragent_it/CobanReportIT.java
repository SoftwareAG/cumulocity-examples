package c8y.trackeragent_it;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;

import c8y.Position;
import c8y.SpeedMeasurement;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.protocol.coban.CobanDeviceMessages;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.protocol.coban.parser.CobanAlarmType;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;
import c8y.trackeragent.utils.message.TrackerMessage;

public class CobanReportIT extends TrackerITSupport {

	private String imei;
	private CobanDeviceMessages deviceMessages = new CobanDeviceMessages();
	private CobanServerMessages serverMessages = new CobanServerMessages();

	@Before
	public void init() throws Exception {
		imei = Devices.randomImei();
		bootstrapDevice(imei, deviceMessages.logon(imei));
	}

	@Override
	protected TrackingProtocol getTrackerProtocol() {
		return TrackingProtocol.COBAN;
	}
	
	@Test
	public void shouldProcessLogonMessage() throws Exception {
		String response = writeInNewConnection(deviceMessages.logon(imei));
		
		assertThat(response).isNotNull();
		TrackerMessage actual = serverMessages.msg(response);
		TrackerMessage expected = serverMessages.load()
				.appendReport(serverMessages.timeIntervalLocationRequest(imei, "03m"));
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void shouldProcessHeartbeatMessage() throws Exception {
		String response = writeInNewConnection(deviceMessages.logon(imei), deviceMessages.heartbeat(imei));

		TrackerMessage actual = serverMessages.msg(response);
		TrackerMessage expected = serverMessages.load()
				.appendReport(serverMessages.timeIntervalLocationRequest(imei, "03m"))
				.appendReport(serverMessages.on());
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void shouldProcessPositionUpdateMessage() throws Exception {
		writeInNewConnection(deviceMessages.logon(imei), deviceMessages.positionUpdate(imei, Positions.TK10xSample));

		assertThat(actualPositionInTracker()).isEqualTo(TK10xCoordinatesTranslator.parse(Positions.TK10xSample));
		assertThat(actualPositionInEvent()).isEqualTo(TK10xCoordinatesTranslator.parse(Positions.TK10xSample));
	}

	@Test
	public void shouldProcessSpeedWithinPositionUpdateMessage() throws Exception {
		// 120 = 65 * CobanParser.COBAN_SPEED_MEASUREMENT_FACTOR
		writeInNewConnection(deviceMessages.logon(imei), deviceMessages.positionUpdate(imei, 65));

		assertThat(actualSpeedInEvent()).isEqualTo(new BigDecimal(120));
	}

	@Test
	public void shouldProcessAlarmMessage() throws Exception {
		writeInNewConnection(deviceMessages.logon(imei), deviceMessages.alarm(imei, CobanAlarmType.LOW_BATTERY));

		Thread.sleep(1000);
		assertThat(findAlarm(imei, CobanAlarmType.LOW_BATTERY)).isNotNull();
	}

	@Test
	public void shouldProcessPositionUpdateNoGpsSignalMessage() throws Exception {
		writeInNewConnection(deviceMessages.logon(imei), deviceMessages.positionUpdateNoGPS(imei));

		assertThat(findAlarm(imei, CobanAlarmType.NO_GPS_SIGNAL)).isNotNull();
	}

	@Test
	public void shouldClearNoGpsSignalAlarm() throws Exception {
		writeInNewConnection(deviceMessages.logon(imei), deviceMessages.positionUpdateNoGPS(imei));
		writeInNewConnection(deviceMessages.logon(imei), deviceMessages.positionUpdate(imei, Positions.TK10xSample));

		assertThat(findAlarm(imei, CobanAlarmType.NO_GPS_SIGNAL)).isNull();
	}

	@Test
	public void shouldProcessOverSpeedMessage() throws Exception {
		writeInNewConnection(deviceMessages.logon(imei), deviceMessages.overSpeedAlarm(imei, 50));

		AlarmRepresentation alarm = findAlarm(imei, CobanAlarmType.OVERSPEED);
		assertThat(alarm).isNotNull();
		// 92 = 50 * CobanParser.COBAN_SPEED_MEASUREMENT_FACTOR
		assertThat(alarm.getText()).isEqualTo("Geschwindigkeitsüberschreitung 92km/h");
	}

	private BigDecimal actualSpeedInEvent() {
		return actualPositionEvent().get(SpeedMeasurement.class).getSpeed().getValue();
	}

	private Position actualPositionInEvent() {
		return actualPositionEvent().get(Position.class);
	}

	private EventRepresentation actualPositionEvent() {
	    return findLastEvent(imei, TrackerDevice.LU_EVENT_TYPE);
	}

	private Position actualPositionInTracker() {
	    return getDeviceMO(imei).get(Position.class);
	}

}
