package com.cumulocity.greenbox.server.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MeasurementEntry {
    private int id;

    @JsonProperty("val")
    private BigDecimal value;

    private boolean offline;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

}
