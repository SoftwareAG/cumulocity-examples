package com.cumulocity.greenbox.server.service;

import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;

public interface MeasurementValuePostProcessor {

    MeasurementValue process(MeasurementValue measurement);
}
