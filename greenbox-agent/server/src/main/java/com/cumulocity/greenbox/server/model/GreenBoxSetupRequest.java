package com.cumulocity.greenbox.server.model;

import java.util.Collection;
import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GreenBoxSetupRequest extends GreenBoxRequest {

    private Collection<IpAddress> ipaddresses = new LinkedList<>();

    @JsonProperty("site_name")
    private String location;

    private Collection<Device> devices = new LinkedList<>();

    @JsonProperty("calculation_points")
    private Collection<CalculationPoint> calculationPoints = new LinkedList<>();

    @JsonProperty("device_points")
    private Collection<DevicePoint> devicePoints = new LinkedList<>();

    @JsonProperty("data_points")
    private Collection<DataPoint> dataPoints = new LinkedList<>();

    public Collection<IpAddress> getIpaddresses() {
        return ipaddresses;
    }

    public void setIpaddresses(Collection<IpAddress> ipaddresses) {
        this.ipaddresses = ipaddresses;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Collection<Device> getDevices() {
        return devices;
    }

    public void setDevices(Collection<Device> devices) {
        this.devices = devices;
    }

    public Collection<CalculationPoint> getCalculationPoints() {
        return calculationPoints;
    }

    public void setCalculationPoints(Collection<CalculationPoint> calculationPoints) {
        this.calculationPoints = calculationPoints;
    }

    public Collection<DevicePoint> getDevicePoints() {
        return devicePoints;
    }

    public void setDevicePoints(Collection<DevicePoint> devicePoints) {
        this.devicePoints = devicePoints;
    }

    public Collection<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(Collection<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    @Override
    public String toString() {
        return "GreenBoxSetupRequest [hubUID=" + getHubUID() + ", type=" + getType() + ", hub=" + getHub() + ", ipaddresses=" + ipaddresses
                + ", location=" + location + ", devices=" + devices + ", calculationPoints=" + calculationPoints + ", devicePoints="
                + devicePoints + ", dataPoints=" + dataPoints + "]";
    }

}
