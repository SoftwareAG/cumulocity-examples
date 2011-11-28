package com.cumulocity.agents.mps.emulator.model;

public class MeterState {
    private boolean status;
    private String result;
    
    public MeterState(boolean status, String result) {
        this.status = status;
        this.result = result;
    }
    
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public boolean getStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
}
