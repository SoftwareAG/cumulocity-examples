package c8y.example.service;

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
        decoderOutput.addMeasurementToCreate(measurement);

        Map<String, Object> seriesMap_2 = new HashMap<>();
        seriesMap_2.put("E",20);

        MeasurementRepresentation measurement_2 = new MeasurementRepresentation();
        measurement_2.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        measurement_2.setType("c8y_DummyFragment");
        measurement_2.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        measurement_2.setProperty("c8y_DummyFragment", seriesMap_2);
        decoderOutput.addMeasurementToCreate(measurement_2);
    }

    private void setUpDeviceProperties(DecoderOutput decoderOutput) {
        decoderOutput.addPropertyToUpdateDeviceMo(new ManagedObjectProperty("deviceSample", "#sampleValue"));
        decoderOutput.addPropertyToUpdateDeviceMo(new ManagedObjectProperty("newDeviceSample", 11,"C"));

        List<ManagedObjectProperty> childProperties = new ArrayList<>();
        childProperties.add(new ManagedObjectProperty("childDeviceSample", "#sampleValue_1"));
        childProperties.add(new ManagedObjectProperty("newChildDeviceSample", 110,"F"));

        decoderOutput.addPropertyToUpdateDeviceMo(new ManagedObjectProperty("newChildDeviceSample", childProperties));
    }

    private void setUpAlarmProperties(DecoderOutput decoderOutput, DecoderInput decoderInput) {
        AlarmRepresentation alarm = new AlarmRepresentation();
        alarm.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        alarm.setType("Type_1");
        alarm.setText("Alarm_Text");
        alarm.setSeverity("WARNING");
        alarm.setStatus(CumulocityAlarmStatuses.ACTIVE.name());
        alarm.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        decoderOutput.addAlarmToCreate(alarm);

        AlarmRepresentation alarm_2 = new AlarmRepresentation();
        alarm_2.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        alarm_2.setType("Type_2");
        alarm_2.setText("Alarm_Text_2");
        alarm_2.setSeverity("CRITICAL");
        alarm_2.setStatus(CumulocityAlarmStatuses.ACTIVE.name());
        alarm_2.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        decoderOutput.addAlarmToCreate(alarm_2);
    }

    private void setUpAlarmTypesToClearProperties(DecoderOutput decoderOutput) {
        decoderOutput.setAlarmTypesToClear(Arrays.asList("Type_1","Type_3"));
    }

    private void setUpEventProperties(DecoderOutput decoderOutput, DecoderInput decoderInput) {
        EventRepresentation event = new EventRepresentation();
        event.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        event.setText("Event_Text");
        event.setType("Event_Type");
        event.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        decoderOutput.addEventToCreate(event);

        EventRepresentation event_2 = new EventRepresentation();
        event_2.setSource(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getDeviceMoId())));
        event_2.setText("Event_Text_2");
        event_2.setType("Event_Type_2");
        event_2.setDateTime(new DateTime(decoderInput.getUpdateTime()));
        decoderOutput.addEventToCreate(event_2);
    }
}
