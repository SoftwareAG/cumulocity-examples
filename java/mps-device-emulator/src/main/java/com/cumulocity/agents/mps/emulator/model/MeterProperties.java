package com.cumulocity.agents.mps.emulator.model;

import java.util.ArrayList;
import java.util.List;

public class MeterProperties {
    
    private boolean status;
    private List<MeterResult> result = new ArrayList<MeterResult>();
    
    public static final MeterProperties SAMPLE_PROPERTIES = new MeterProperties();
    
    static {
        SAMPLE_PROPERTIES.setStatus(true);
        SAMPLE_PROPERTIES.getResult().add(new MeterResult(1,1,0,0,1));
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<MeterResult> getResult() {
        return result;
    }

    public void setResult(List<MeterResult> result) {
        this.result = result;
    }
}
