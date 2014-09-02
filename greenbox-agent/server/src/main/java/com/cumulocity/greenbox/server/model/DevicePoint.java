package com.cumulocity.greenbox.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DevicePoint {
    @JsonProperty("data_point_id")
    private int dataPointId;

    @JsonProperty("datatype")
    private String dataType;

    private String label;

    private int address;

    private String type;

    private String unit;

    public int getDataPointId() {
        return dataPointId;
    }

    public void setDataPointId(int dataPointId) {
        this.dataPointId = dataPointId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int adress) {
        this.address = adress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}
