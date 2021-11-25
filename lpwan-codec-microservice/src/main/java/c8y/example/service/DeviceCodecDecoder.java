package c8y.example.service;

import c8y.example.util.DecodedObject;
import com.cumulocity.lpwan.codec.Decoder;
import com.cumulocity.lpwan.codec.exception.DecoderException;
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

import java.util.*;

@Component
public class DeviceCodecDecoder implements Decoder {

    @Override
    public DecoderOutput decode(DecoderInput decoderInput) throws DecoderException {

        DecoderOutput decoderOutput = new DecoderOutput();

        setUpMeasurementProperties(decoderOutput,decoderInput);

        setUpEventProperties(decoderOutput, decoderInput);

        setUpAlarmProperties(decoderOutput, decoderInput);

        setUpAlarmTypesToClearProperties(decoderOutput);

        setUpDeviceProperties(decoderOutput);

        return decoderOutput;
    }

    private void setUpMeasurementProperties(DecoderOutput decoderOutput, DecoderInput decoderInput) {
//        List<MeasurementRepresentation> measurements = new ArrayList<>();
        Map<String, Object> valuesMap = new HashMap<>();
        valuesMap.put("value",15);
        valuesMap.put("unit","C");

        Map<String, Object> seriesMap = new HashMap<>();
        seriesMap.put("T",valuesMap);

        MeasurementRepresentation measurement = new MeasurementRepresentation();
        measurement.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        measurement.setType("c8y_Temperature");
        measurement.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        measurement.setProperty("c8y_Temperature", seriesMap);
        decoderOutput.addMeasurement(measurement);
//        measurements.add(measurement);

        Map<String, Object> seriesMap_2 = new HashMap<>();
        seriesMap_2.put("E",20);

        MeasurementRepresentation measurement_2 = new MeasurementRepresentation();
        measurement_2.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        measurement_2.setType("c8y_DummyFragment");
        measurement_2.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        measurement_2.setProperty("c8y_DummyFragment", seriesMap_2);
        decoderOutput.addMeasurement(measurement_2);
//        measurements.add(measurement_2);

//        decoderOutput.setMeasurementsToCreate(measurements);
    }

    private void setUpDeviceProperties(DecoderOutput decoderOutput) {
//        List<ManagedObjectProperty> managedObjectProperties = new ArrayList<>();
//        managedObjectProperties.add(new ManagedObjectProperty("deviceSample", "#sampleValue"));
//        managedObjectProperties.add(new ManagedObjectProperty("newDeviceSample", 11,"C"));
        decoderOutput.addManagedObjectProperty(new ManagedObjectProperty("deviceSample", "#sampleValue"));
        decoderOutput.addManagedObjectProperty(new ManagedObjectProperty("newDeviceSample", 11,"C"));

        List<ManagedObjectProperty> childProperties = new ArrayList<>();
        childProperties.add(new ManagedObjectProperty("childDeviceSample", "#sampleValue_1"));
        childProperties.add(new ManagedObjectProperty("newChildDeviceSample", 110,"F"));

//        managedObjectProperties.add(new ManagedObjectProperty("newChildDeviceSample", childProperties));
        decoderOutput.addManagedObjectProperty(new ManagedObjectProperty("newChildDeviceSample", childProperties));

//        decoderOutput.setPropertiesToUpdateDeviceMo(managedObjectProperties);
    }

    private void setUpAlarmProperties(DecoderOutput decoderOutput, DecoderInput decoderInput) {
//        List<AlarmRepresentation> alarms =  new ArrayList<>();

        AlarmRepresentation alarm = new AlarmRepresentation();
        alarm.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        alarm.setType("Type_1");
        alarm.setText("Alarm_Text");
        alarm.setSeverity("WARNING");
        alarm.setStatus(CumulocityAlarmStatuses.ACTIVE.name());
        alarm.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        decoderOutput.addAlarm(alarm);
//        alarms.add(alarm);

        AlarmRepresentation alarm_2 = new AlarmRepresentation();
        alarm_2.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        alarm_2.setType("Type_2");
        alarm_2.setText("Alarm_Text_2");
        alarm_2.setSeverity("CRITICAL");
        alarm_2.setStatus(CumulocityAlarmStatuses.ACTIVE.name());
        alarm_2.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        decoderOutput.addAlarm(alarm_2);
//        alarms.add(alarm_2);

//        decoderOutput.setAlarmsToCreate(alarms);
    }

    private void setUpAlarmTypesToClearProperties(DecoderOutput decoderOutput) {
        decoderOutput.setAlarmTypesToClear(Arrays.asList("Type_1","Type_3"));
    }

    private void setUpEventProperties(DecoderOutput decoderOutput, DecoderInput decoderInput) {
//        List<EventRepresentation> events = new ArrayList<>();

        EventRepresentation event = new EventRepresentation();
        event.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        event.setText("Event_Text");
        event.setType("Event_Type");
        event.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        decoderOutput.addEvent(event);
//        events.add(event);

        EventRepresentation event_2 = new EventRepresentation();
        event_2.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        event_2.setText("Event_Text_2");
        event_2.setType("Event_Type_2");
        event_2.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        decoderOutput.addEvent(event_2);
//        events.add(event_2);

//        decoderOutput.setEventsToCreate(events);
    }
}
