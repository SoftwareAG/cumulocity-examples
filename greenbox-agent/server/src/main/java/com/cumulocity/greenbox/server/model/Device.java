package com.cumulocity.greenbox.server.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Device {

    @JsonProperty("device_address")
    private int address;

    @JsonProperty("device_type_no")
    private int typeNumber;

    private String description;

    @JsonProperty("device_name")
    private String name;

    @JsonProperty("device_points")
    private List<DevicePoint> points;

    @JsonProperty("device_type_name")
    private String type;

    @JsonProperty("device_id")
    private int id;

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getTypeNumber() {
        return typeNumber;
    }

    public void setTypeNumber(int typeNumber) {
        this.typeNumber = typeNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DevicePoint> getPoints() {
        return points;
    }

    public void setPoints(List<DevicePoint> points) {
        this.points = points;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
