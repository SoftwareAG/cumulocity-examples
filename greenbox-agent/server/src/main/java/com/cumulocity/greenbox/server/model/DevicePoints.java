package com.cumulocity.greenbox.server.model;

import java.util.List;

import org.svenson.JSONTypeHint;

public class DevicePoints {

    private List<DevicePoint> points;

    public DevicePoints(List<DevicePoint> points) {
        this.points = points;
    }

    @JSONTypeHint(DevicePoint.class)
    public List<DevicePoint> getPoints() {
        return points;
    }

    public void setPoints(List<DevicePoint> points) {
        this.points = points;
    }
}
