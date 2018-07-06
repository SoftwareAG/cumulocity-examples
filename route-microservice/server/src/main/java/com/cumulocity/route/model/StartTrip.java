package com.cumulocity.route.model;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.route.model.core.HasMeasurement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StartTrip extends HasMeasurement {
    private final MeasurementRepresentation measurement;
    private final ManagedObjectRepresentation device;
}
