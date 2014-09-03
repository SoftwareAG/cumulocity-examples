package com.cumulocity.greenbox.server.model;

import java.util.List;

import org.joda.time.DateTime;

import com.cumulocity.greenbox.server.model.json.DateTimeConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class Measurement {

    @JsonDeserialize(converter = DateTimeConverter.class)
    private DateTime time;

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    @JsonProperty("data_points")
    private List<MeasurementEntry> dataPoints;

    public List<MeasurementEntry> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<MeasurementEntry> dataPoints) {
        this.dataPoints = dataPoints;
    }

    @Override
    public String toString() {
        return "Measurement [time=" + time + ", dataPoints=" + dataPoints + "]";
    }

}
