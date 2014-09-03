package com.cumulocity.greenbox.server.service;

import static com.cumulocity.rest.representation.inventory.ManagedObjects.asManagedObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;

class UniqueMeasurmentsFactory {

    private final Map<MeasurementWithSourceAndTime, MeasurementRepresentation> storage = new HashMap<MeasurementWithSourceAndTime, MeasurementRepresentation>();

    public MeasurementRepresentation get(MeasurementWithSourceAndTime id) {
        if (!storage.containsKey(id)) {
            MeasurementRepresentation newMeasurement = new MeasurementRepresentation();
            newMeasurement.setTime(id.getTime().toDate());
            newMeasurement.setSource(asManagedObject(GId.asGId(id.getSource())));
            newMeasurement.setType("c8y_greenbox_Measurment");
            storage.put(id, newMeasurement);
        }
        return storage.get(id);
    }

    public Collection<MeasurementRepresentation> getMeasurments() {
        return storage.values();
    }

}