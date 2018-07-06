package com.cumulocity.route.model.core;

import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Getter
@RequiredArgsConstructor
public class MeasurementCreated extends HasMeasurement {
    private final MeasurementRepresentation measurement;

    public BigDecimal getNumber(String fragmentName, String serieName) {
        final Map fragment = (Map) measurement.get(fragmentName);
        if (fragment != null) {
            Map serie = (Map) fragment.get(serieName);
            if (serie != null) {
                Object value = serie.get("value");
                if (value != null) {
                    return new BigDecimal(String.valueOf(value));
                }
            }
        }

        return null;
    }

    public boolean existsFragment(String fragmentName) {
        return getObject(fragmentName) != null;
    }

    public Map getObject(String fragmentName) {
        return (Map) measurement.get(fragmentName);
    }
}
