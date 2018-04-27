package com.cumulocity.route.model.core;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import lombok.SneakyThrows;

import java.util.Objects;

import static com.cumulocity.route.configuration.ObjectMapperConfiguration.baseObjectMapper;

public abstract class HasMeasurement extends HasSource {
    public abstract MeasurementRepresentation getMeasurement();

    public GId getSource() {
        if (getMeasurement() != null && getMeasurement().getSource() != null) {
            return getMeasurement().getSource().getId();
        }
        return null;
    }

    @SneakyThrows
    public String toString() {
        StringBuilder result = new StringBuilder(getClass().getSimpleName());
        if (getMeasurement() != null) {
            result.append("(measurement:").append(baseObjectMapper.writeValueAsString(getMeasurement())).append(")");
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!Objects.equals(getClass(), obj.getClass())) {
            return false;
        }
        HasMeasurement other = (HasMeasurement) obj;
        if (!Objects.equals(getSource(), other.getSource())) {
            return false;
        }
        if (!Objects.equals(getMeasurement().getAttrs(), other.getMeasurement().getAttrs())) {
            return false;
        }
        return true;
    }
}
