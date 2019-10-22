package c8y.example.decoders.json.util;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjects;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@Slf4j
public class MeasurementTest {

    public final static HashMap<String, Double> mapping = new HashMap<>();
    static {
        mapping.put("c8y_TotalEnergy.Active.value", 1176.43);
        mapping.put("c8y_TotalEnergy.forwardActive.value", 1176.43);
        mapping.put("c8y_TotalEnergy.reverseActive.value", 0.0);
        mapping.put("c8y_TotalEnergy.forwardReactive.value", 207.97);
        mapping.put("c8y_TotalEnergy.reverseReactive.value", 0.0);
        mapping.put("c8y_ForwardActiveEnergy.T1.value", 1175.37);
        mapping.put("c8y_ForwardActiveEnergy.T2.value", 1.07);
        mapping.put("c8y_ForwardActiveEnergy.T3.value", 0.0);
        mapping.put("c8y_ForwardActiveEnergy.T4.value", 0.0);
        mapping.put("c8y_Voltage.phaseA.value", 242.88);
        mapping.put("c8y_Voltage.phaseB.value", 242.84);
        mapping.put("c8y_Voltage.phaseC.value", 242.98);
        mapping.put("c8y_Current.phaseA.value", 0.2);
        mapping.put("c8y_Current.phaseB.value", 0.2);
        mapping.put("c8y_Current.phaseC.value", 0.16);
        mapping.put("c8y_ActivePower.Total.value", 0.07);
        mapping.put("c8y_ActivePower.phaseA.value", 0.03);
        mapping.put("c8y_ActivePower.phaseB.value", 0.02);
        mapping.put("c8y_ActivePower.phaseC.value", 0.02);
        mapping.put("c8y_ReactivePower.total.value", -0.11);
        mapping.put("c8y_ReactivePower.phaseA.value", -0.04);
        mapping.put("c8y_ReactivePower.phaseB.value", -0.04);
        mapping.put("c8y_ReactivePower.phaseC.value", -0.03);
        mapping.put("c8y_ApparentPower.Total.value", 0.13);
        mapping.put("c8y_ApparentPower.phaseA.value", 0.05);
        mapping.put("c8y_ApparentPower.phaseB.value", 0.04);
        mapping.put("c8y_ApparentPower.phaseC.value", 0.04);
        mapping.put("c8y_PowerFactor.total.value", 0.57);
        mapping.put("c8y_PowerFactor.phaseA.value", 0.52);
        mapping.put("c8y_PowerFactor.phaseB.value", 0.58);
        mapping.put("c8y_PowerFactor.phaseC.value", 0.62);
        mapping.put("c8y_PowerLine.frequency.value", 49.99);
    }

    @Test
    public void setAndGet() {
        Measurement m = new Measurement();
        m.setSource(ManagedObjects.asManagedObject(new GId("12345")));
        m.setType("c8y_PowerMeterTelemetry");
        m.setDateTime(new DateTime(0).toDateTime(DateTimeZone.UTC));
        for(Map.Entry<String, Double> entry: mapping.entrySet())
            m.set(entry.getKey(), entry.getValue());
        for(Map.Entry<String, Double> entry: mapping.entrySet())
            assertEquals(m.get(entry.getKey()),entry.getValue());
    }
}