package c8y.example.decoders.json.util;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjects;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MeasurementTest {

    private final static HashMap<String, Float> mapping = new HashMap<>();
    static {
        mapping.put("c8y_TotalEnergy.Active.value", 0.1f);
        mapping.put("c8y_TotalEnergy.forwardActive.value", 0.2f);
        mapping.put("c8y_TotalEnergy.reverseActive.value", 0.3f);
        mapping.put("c8y_TotalEnergy.forwardReactive.value", 0.4f);
        mapping.put("c8y_TotalEnergy.reverseReactive.value", 0.5f);
        mapping.put("c8y_ForwardActiveEnergy.T1.value", 0.6f);
        mapping.put("c8y_ForwardActiveEnergy.T2.value", 0.7f);
        mapping.put("c8y_ForwardActiveEnergy.T3.value", 0.8f);
        mapping.put("c8y_ForwardActiveEnergy.T4.value", 0.9f);
        mapping.put("c8y_Voltage.phaseA.value", 1.0f);
        mapping.put("c8y_Voltage.phaseB.value", 1.1f);
        mapping.put("c8y_Voltage.phaseC.value", 1.2f);
        mapping.put("c8y_Current.phaseA.value", 1.3f);
        mapping.put("c8y_Current.phaseB.value", 1.4f);
        mapping.put("c8y_Current.phaseC.value", 1.5f);
        mapping.put("c8y_ActivePower.Total.value", 1.6f);
        mapping.put("c8y_ActivePower.phaseA.value", 1.7f);
        mapping.put("c8y_ActivePower.phaseB.value", 1.8f);
        mapping.put("c8y_ActivePower.phaseC.value", 1.9f);
        mapping.put("c8y_ReactivePower.total.value", 2.0f);
        mapping.put("c8y_ReactivePower.phaseA.value", 2.1f);
        mapping.put("c8y_ReactivePower.phaseB.value", 2.2f);
        mapping.put("c8y_ReactivePower.phaseC.value", 2.3f);
        mapping.put("c8y_ApparentPower.Total.value", 2.4f);
        mapping.put("c8y_ApparentPower.phaseA.value", 2.5f);
        mapping.put("c8y_ApparentPower.phaseB.value", 2.6f);
        mapping.put("c8y_ApparentPower.phaseC.value", 2.7f);
        mapping.put("c8y_PowerFactor.total.value", 2.8f);
        mapping.put("c8y_PowerFactor.phaseA.value", 2.9f);
        mapping.put("c8y_PowerFactor.phaseB.value", 3.0f);
        mapping.put("c8y_PowerFactor.phaseC.value", 3.1f);
        mapping.put("c8y_PowerLine.frequency.value", 3.2f);
    }

    @Test
    public void set() {
        Measurement m = new Measurement();
        m.setSource(ManagedObjects.asManagedObject(new GId("12345")));
        m.setType("c8y_PowerMeterTelemetry");
        m.setDateTime(new DateTime(0).toDateTime(DateTimeZone.UTC));
        for(Map.Entry<String, Float> entry: mapping.entrySet())
            m.set(entry.getKey(), entry.getValue());
        log.info(m.toJSON());
    }
}