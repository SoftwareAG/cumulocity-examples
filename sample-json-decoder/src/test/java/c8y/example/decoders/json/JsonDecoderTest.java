package c8y.example.decoders.json;

import c8y.example.decoders.json.util.Measurement;
import c8y.example.decoders.json.util.MeasurementTest;
import com.cumulocity.microservice.customdecoders.api.exception.DecoderServiceException;
import com.cumulocity.microservice.customdecoders.api.model.DecoderInputData;
import com.cumulocity.microservice.customdecoders.api.model.DecoderResult;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JsonDecoderTest {

    private final String msg = "" +
            "{" +
                "\"ts\":1550012651000," +
                "\"values\":{" +
                    "\"totalActiveEnergy\":1176.43," +
                    "\"totalForwardActiveEnergy\":1176.43," +
                    "\"totalReverseActiveEnergy\":0.0," +
                    "\"totalForwardReactiveEnergy\":207.97," +
                    "\"totalReverseReactiveEnergy\":0.0," +
                    "\"forwardActiveEnergyT1\":1175.37," +
                    "\"forwardActiveEnergyT2\":1.07," +
                    "\"forwardActiveEnergyT3\":0.0," +
                    "\"forwardActiveEnergyT4\":0.0," +
                    "\"voltagePhaseA\":242.88," +
                    "\"voltagePhaseB\":242.84," +
                    "\"voltagePhaseC\":242.98," +
                    "\"currentPhaseA\":0.2," +
                    "\"currentPhaseB\":0.2," +
                    "\"currentPhaseC\":0.16," +
                    "\"totalActivePower\":0.07," +
                    "\"activePowerPhaseA\":0.03," +
                    "\"activePowerPhaseB\":0.02," +
                    "\"activePowerPhaseC\":0.02," +
                    "\"totalReactivePower\":-0.11," +
                    "\"reactivePowerPhaseA\":-0.04," +
                    "\"reactivePowerPhaseB\":-0.04," +
                    "\"reactivePowerPhaseC\":-0.03," +
                    "\"totalApparentPower\":0.13," +
                    "\"apparentPowerPhaseA\":0.05," +
                    "\"apparentPowerPhaseB\":0.04," +
                    "\"apparentPowerPhaseC\":0.04," +
                    "\"totalPowerFactor\":0.57," +
                    "\"powerFactorPhaseA\":0.52," +
                    "\"powerFactorPhaseB\":0.58," +
                    "\"powerFactorPhaseC\":0.62," +
                    "\"frequency\":49.99" +
                "}" +
            "}";
    private final DecoderInputData inputData = new DecoderInputData();
    private final GId id = new GId("12345");

    private MeasurementApi measurementApi = mock(MeasurementApi.class);
    private DecoderResult decoderResult;

    private ArrayList<MeasurementRepresentation> measurementsCreated = new ArrayList<>();

    @Before
    public void setUp() throws DecoderServiceException {
        when(measurementApi.create(any(MeasurementRepresentation.class))).thenAnswer(new Answer<Object>() {
            @Override
            public MeasurementRepresentation answer(InvocationOnMock invocation) {
                MeasurementRepresentation m = (MeasurementRepresentation) invocation.getArguments()[0];
                measurementsCreated.add(m);
                return m;
            }
        });

        inputData.setValue(msg);
        inputData.setSourceDeviceId(id.getValue());
        decoderResult = new JsonDecoder(new JsonDecoderService(measurementApi)).decodeWithJSONInput(inputData);
    }

    @Test
    public void validateMeasurements() {
        assertEquals(1, measurementsCreated.size());
        Measurement measurement = (Measurement)measurementsCreated.get(0);
        for (String path: JsonDecoderService.mapping.values()) {
            assertEquals(MeasurementTest.mapping.get(path), measurement.get(path));
        }
    }


    @Test
    public void validateDecoderResult() {
        assertNull(decoderResult.getAlarms());
        assertNull(decoderResult.getEvents());
        assertNull(decoderResult.getDataFragments());
        assertNull(decoderResult.getMeasurements());
    }

}