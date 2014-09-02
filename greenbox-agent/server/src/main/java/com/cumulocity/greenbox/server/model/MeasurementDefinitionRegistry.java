package com.cumulocity.greenbox.server.model;

import java.util.HashMap;
import java.util.Map;

import org.svenson.JSONTypeHint;

public class MeasurementDefinitionRegistry {

    private Map<Integer, MeasurementDefinition> definitions = new HashMap<Integer, MeasurementDefinition>();

    public MeasurementDefinition get(int datapoint) {
        return definitions.get(datapoint);
    }

    public void save(int dataPoint, MeasurementDefinition definition) {
        definitions.put(dataPoint, definition);
    }

    @JSONTypeHint(MeasurementDefinition.class)
    public Map<Integer, MeasurementDefinition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<Integer, MeasurementDefinition> definitions) {
        this.definitions = definitions;
    }
}