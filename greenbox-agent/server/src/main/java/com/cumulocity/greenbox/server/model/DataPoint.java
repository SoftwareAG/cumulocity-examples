package com.cumulocity.greenbox.server.model;

import java.util.Collections;
import java.util.Map;

import com.cumulocity.model.measurement.MeasurementValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataPoint {

    @JsonProperty("data_point_id")
    private String dataPointId;

    private String description;

    private boolean chart;

    @JsonProperty("reg_type")
    private String regType;

    private String address;

    @JsonProperty("device_id")
    private String deviceId;

    private String unit;

    private String name;

    public String getDataPointId() {
        return dataPointId;
    }

    public void setDataPointId(String dataPointId) {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String  address) {
        this.address = address;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String  deviceId) {
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
