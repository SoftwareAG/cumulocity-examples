package com.cumulocity.greenbox.server.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CalculationPoint {

    @JsonProperty("calc_point_id")
    private int id;

    @JsonProperty("data_point_id")
    private int dataPointId;

    @JsonProperty("data_points")
    private List<DataPointReference> dataPoints;

    private int precision;

    private String unit;

    private String expression;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDataPointId() {
        return dataPointId;
    }

    public void setDataPointId(int dataPointId) {
        this.dataPointId = dataPointId;
    }

    public List<DataPointReference> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<DataPointReference> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

}
