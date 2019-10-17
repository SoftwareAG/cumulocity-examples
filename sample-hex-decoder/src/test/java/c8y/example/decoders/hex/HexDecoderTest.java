package c8y.example.decoders.hex;

import c8y.Position;
import c8y.example.decoders.hex.util.Measurement;
import c8y.example.decoders.hex.util.Message;
import com.cumulocity.microservice.customdecoders.api.model.DecoderInputData;
import com.cumulocity.microservice.customdecoders.api.model.DecoderResult;
import com.cumulocity.model.event.CumulocitySeverities;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HexDecoderTest {

    private final String msg_str = "6017424ce92a40d8c0260D13620f";
    private final DecoderInputData inputData= new DecoderInputData();
    private final Message msg = new Message(msg_str);
    private final GId id = new GId("12345");

    private MeasurementApi measurementApi = mock(MeasurementApi.class);
    private InventoryApi inventoryApi = mock(InventoryApi.class);
    private DecoderResult decoderResult;

    private ArrayList<MeasurementRepresentation> measurementsCreated = new ArrayList<>();
    private ArrayList<ManagedObjectRepresentation> inventoriesUpdated = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        when(measurementApi.create(any(MeasurementRepresentation.class))).thenAnswer(new Answer<MeasurementRepresentation>() {
            @Override
            public MeasurementRepresentation answer(InvocationOnMock invocation) {
                MeasurementRepresentation m = (MeasurementRepresentation) invocation.getArguments()[0];
                measurementsCreated.add(m);
                return m;
            }
        });

        when(inventoryApi.update(any(ManagedObjectRepresentation.class))).thenAnswer(new Answer<ManagedObjectRepresentation>() {
            @Override
            public ManagedObjectRepresentation answer(InvocationOnMock invocation) {
                ManagedObjectRepresentation mo = (ManagedObjectRepresentation) invocation.getArguments()[0];
                inventoriesUpdated.add(mo);
                return mo;
            }
        });

        inputData.setValue(msg_str);
        inputData.setSourceDeviceId(id.getValue());
        decoderResult = new HexDecoder(new HexDecoderService(measurementApi, inventoryApi)).decodeWithJSONInput(inputData);
    }

    @Test
    public void validateMeasurements(){
        assertEquals(1, measurementsCreated.size());
        Measurement measurement = (Measurement)measurementsCreated.get(0);
        assertEquals(id.getValue(), measurement.getSource().getId().getValue());
        assertEquals("c8y_LoraDemonstratorTelemetry", measurement.getType());
        assertEquals(msg.getTemperature(), measurement.get("c8y_Temperature.T.value"));
        assertEquals(msg.getBatteryVoltage(), measurement.get("c8y_Battery.voltage.value"));
        assertEquals(msg.getRSSI(), measurement.get("c8y_SignalStrength.RSSI.value"));
        assertEquals(msg.getSNR(), measurement.get("c8y_SignalStrength.SNR.value"));
    }

    @Test
    public void validateInventoryUpdates() {
        assertEquals(1, inventoriesUpdated.size());
        ManagedObjectRepresentation update = inventoriesUpdated.get(0);
        Position pos = update.get(Position.class);
        assertEquals(BigDecimal.valueOf(msg.getLatitude()), pos.getLat());
        assertEquals(BigDecimal.valueOf(msg.getLongtitude()), pos.getLng());
    }

    @Test
    public void validateAlarms() {
        assertEquals(1, decoderResult.getAlarms().size());
        AlarmRepresentation alarm= decoderResult.getAlarms().get(0);
        assertEquals(CumulocitySeverities.MAJOR.toString(), alarm.getSeverity());
        assertEquals("c8y_AccelerometerAlarm", alarm.getType());
        assertEquals("Transmission was triggered by accelerometer", alarm.getText());
    }

    @Test
    public void validateEvents() {
        assertEquals(2, decoderResult.getEvents().size());
        int buttonEvents = 0;
        int locationEvents = 0;
        for (EventRepresentation event: decoderResult.getEvents()) {
            assertEquals(id.getValue(), event.getSource().getId().getValue());
            switch (event.getType()) {
                case "c8y_ButtonEvent":
                    buttonEvents++;
                    assertEquals("Transmission was triggered by button press", event.getText());
                    break;
                case "c8y_LocationUpdate":
                    locationEvents++;
                    Position pos = event.get(Position.class);
                    assertEquals(BigDecimal.valueOf(msg.getLatitude()), pos.getLat());
                    assertEquals(BigDecimal.valueOf(msg.getLongtitude()), pos.getLng());
                    break;
                default:
                    fail(String.format("Unexpected event type \"%s\"", event.getType()));
            }
        }
        assertEquals(1, buttonEvents);
        assertEquals(1, locationEvents);
    }

}