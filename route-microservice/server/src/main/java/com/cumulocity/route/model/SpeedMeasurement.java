package com.cumulocity.route.model;

import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.route.model.core.HasMeasurement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class SpeedMeasurement extends HasMeasurement {
    private final MeasurementRepresentation measurement;
    private final BigDecimal value;

    public boolean valueIsGreaterThan(int value) {
        return getValue().doubleValue() > value;
    }
}
