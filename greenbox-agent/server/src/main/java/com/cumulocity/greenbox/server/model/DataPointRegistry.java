package com.cumulocity.greenbox.server.model;

import java.util.HashMap;
import java.util.Map;

import org.svenson.JSONTypeHint;

public class DataPointRegistry {

    private Map<String, DataPoint> storage = new HashMap<String, DataPoint>();

    public DataPoint get(String datapoint) {
        return storage.get(datapoint);
    }

    public void save(String dataPoint, DataPoint definition) {
        storage.put(dataPoint, definition);
    }

    @JSONTypeHint(DataPoint.class)
    public Map<String, DataPoint> getStorage() {
        return storage;
    }

    public void setStorage(Map<String, DataPoint> definitions) {
        this.storage = definitions;
    }

    public int size() {
        return storage.size();
    }
}
