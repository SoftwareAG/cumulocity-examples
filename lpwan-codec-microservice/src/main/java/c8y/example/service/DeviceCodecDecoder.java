package c8y.example.service;

import com.cumulocity.lpwan.codec.Decoder;
import com.cumulocity.lpwan.codec.model.DecoderInput;
import com.cumulocity.lpwan.codec.model.DecoderOutput;
import com.cumulocity.lpwan.codec.model.ManagedObjectProperty;
import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjects;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class should provide the decoding logic for the device(s) and the relevant Measurements, Events, Alarms
 * and the properties to update the managed object of the device.
 */

@Component
public class DeviceCodecDecoder implements Decoder {

    @Override
    public DecoderOutput decode(DecoderInput decoderInput) {

        DecoderOutput decoderOutput = new DecoderOutput();

        setUpMeasurementProperties(decoderOutput,decoderInput);
        setUpEventProperties(decoderOutput, decoderInput);
        setUpAlarmProperties(decoderOutput, decoderInput);
        setUpAlarmTypesToClearProperties(decoderOutput);
        setUpDeviceProperties(decoderOutput);

        return decoderOutput;
    }

    private void setUpMeasurementProperties(DecoderOutput decoderOutput, DecoderInput decoderInput) {
        Map<String, Object> valuesMap_Temperature = new HashMap<>();
        valuesMap_Temperature.put("value",45);
        valuesMap_Temperature.put("unit","C");

        Map<String, Object> seriesMap_Temperature = new HashMap<>();
        seriesMap_Temperature.put("T",valuesMap_Temperature);

        MeasurementRepresentation TemperatureMeasurement = new MeasurementRepresentation();
        TemperatureMeasurement.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        TemperatureMeasurement.setType("c8y_Temperature");
        TemperatureMeasurement.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        TemperatureMeasurement.setProperty("c8y_Temperature", seriesMap_Temperature);
        decoderOutput.addMeasurementToCreate(TemperatureMeasurement);

        Map<String, Object> seriesMap_Humidity = new HashMap<>();
        seriesMap_Humidity.put("H",80);

        MeasurementRepresentation humidityMeasurement = new MeasurementRepresentation();
        humidityMeasurement.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        humidityMeasurement.setType("c8y_Humidity");
        humidityMeasurement.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        humidityMeasurement.setProperty("c8y_Humidity", seriesMap_Humidity);
        decoderOutput.addMeasurementToCreate(humidityMeasurement);
    }

    private void setUpDeviceProperties(DecoderOutput decoderOutput) {
        decoderOutput.addPropertyToUpdateDeviceMo(new ManagedObjectProperty("c8y_SignalStrength", "LOW"));
        decoderOutput.addPropertyToUpdateDeviceMo(new ManagedObjectProperty("c8y_BatteryLevel", 35,"mAH"));

        List<ManagedObjectProperty> memoryUsageProperties = new ArrayList<>();
        memoryUsageProperties.add(new ManagedObjectProperty("c8y_CpuUsage", "HIGH"));
        memoryUsageProperties.add(new ManagedObjectProperty("c8y_Memory", 512,"MB"));

        decoderOutput.addPropertyToUpdateDeviceMo(new ManagedObjectProperty("c8y_Memory", memoryUsageProperties));
    }

    private void setUpAlarmProperties(DecoderOutput decoderOutput, DecoderInput decoderInput) {
        AlarmRepresentation temperatureAlarm = new AlarmRepresentation();
        temperatureAlarm.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        temperatureAlarm.setType("c8y_Temperature");
        temperatureAlarm.setText("Temperature is going above the limit.");
        temperatureAlarm.setSeverity("WARNING");
        temperatureAlarm.setStatus(CumulocityAlarmStatuses.ACTIVE.name());
        temperatureAlarm.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        decoderOutput.addAlarmToCreate(temperatureAlarm);

        AlarmRepresentation humidityAlarm = new AlarmRepresentation();
        humidityAlarm.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        humidityAlarm.setType("c8y_Humidity");
        humidityAlarm.setText("Humidity is above the limit.");
        humidityAlarm.setSeverity("CRITICAL");
        humidityAlarm.setStatus(CumulocityAlarmStatuses.ACTIVE.name());
        humidityAlarm.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        decoderOutput.addAlarmToCreate(humidityAlarm);
    }

    private void setUpAlarmTypesToClearProperties(DecoderOutput decoderOutput) {
        decoderOutput.addAlarmTypeToClear("c8y_Temperature");
        decoderOutput.addAlarmTypeToClear("c8y_Speed");
    }

    private void setUpEventProperties(DecoderOutput decoderOutput, DecoderInput decoderInput) {
        EventRepresentation temperatureEvent = new EventRepresentation();
        temperatureEvent.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        temperatureEvent.setText("Temperature is above the limit.");
        temperatureEvent.setType("c8y_Temperature");
        temperatureEvent.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        decoderOutput.addEventToCreate(temperatureEvent);

        EventRepresentation humidityEvent = new EventRepresentation();
        humidityEvent.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        humidityEvent.setText("Humidity is above the limit");
        humidityEvent.setType("c8y_Humidity");
        humidityEvent.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        decoderOutput.addEventToCreate(humidityEvent);
    }
}
