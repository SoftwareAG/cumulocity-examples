package com.cumulocity.agents.mps.emulator.model;


public class MeterProfileResult {
    private String timestamp;
    private Double value;
    
    public MeterProfileResult(String timestamp, Double value) {
        this.timestamp = timestamp;
        this.value = value;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public Double getValue() {
        return value;
    }
    public void setValue(Double value) {
        this.value = value;
    }
}
