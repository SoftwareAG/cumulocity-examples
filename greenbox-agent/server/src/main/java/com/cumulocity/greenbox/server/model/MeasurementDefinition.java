package com.cumulocity.greenbox.server.model;

import java.util.Collections;
import java.util.Map;

import com.cumulocity.model.measurement.MeasurementValue;

public class MeasurementDefinition {

    private String name;

    private String unit;

    public MeasurementDefinition(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Map<String, MeasurementValue> toMeasurementValue(MeasurementEntry value) {
        return Collections.singletonMap(name, new MeasurementValue(value.getValue(), unit, null, null, null));
    }

}