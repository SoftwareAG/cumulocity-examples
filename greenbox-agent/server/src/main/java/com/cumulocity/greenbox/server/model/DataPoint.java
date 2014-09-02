package com.cumulocity.greenbox.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataPoint {

    @JsonProperty("data_point_id")
    private int dataPointId;

    private String description;

    private boolean chart;

    @JsonProperty("reg_type")
    private String regType;

    private int address;

    @JsonProperty("device_id")
    private int deviceId;

    private String unit;

    private String name;

    public int getDataPointId() {
        return dataPointId;
    }

    public void setDataPointId(int dataPointId) {
        this.dataPointId = dataPointId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChart() {
        return chart;
    }

    public void setChart(boolean chart) {
        this.chart = chart;
    }

    public String getRegType() {
        return regType;
    }

    public void setRegType(String reqType) {
        this.regType = reqType;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
