package com.cumulocity.agents.mps.emulator.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MeterProfile {
    private boolean status;
    private List<MeterProfileResult> result = new ArrayList<MeterProfileResult>();
    private static final long THREE_HOURS_IN_MILLIS = 10800000;
    
    
    public static MeterProfile createSampleMeterProfile(Date start, Date end) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long difference = end.getTime() - start.getTime(); 
        MeterProfile profile = new MeterProfile();
        profile.setStatus(true);
        for (int i = 1; i < 10; i++) {
            if (difference > i*THREE_HOURS_IN_MILLIS) {
                profile.getResult().add(new MeterProfileResult(dateFormat.format(new Date(start.getTime() + (i*THREE_HOURS_IN_MILLIS))), 0.0));
            } else {
                break;
            }
        }
        return profile;
    }
    
    public MeterProfile() {}
    
    public MeterProfile(boolean status, List<MeterProfileResult> result) {
        this.status = status;
        this.result = result;
    }
    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public List<MeterProfileResult> getResult() {
        return result;
    }
    public void setResult(List<MeterProfileResult> result) {
        this.result = result;
    }
}
