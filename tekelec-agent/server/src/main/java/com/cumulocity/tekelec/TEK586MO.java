package com.cumulocity.tekelec;

import java.util.List;

public class TEK586MO {

    private List<String> contactReason;
    private List<String> alarmAndStatus;
    
    public TEK586MO() {
    }

    public TEK586MO(List<String> contactReason, List<String> alarmAndStatus) {
        this.contactReason = contactReason;
        this.alarmAndStatus = alarmAndStatus;
    }

    public List<String> getContactReason() {
        return contactReason;
    }

    public void setContactReason(List<String> contactReason) {
        this.contactReason = contactReason;
    }

    public List<String> getAlarmAndStatus() {
        return alarmAndStatus;
    }

    public void setAlarmAndStatus(List<String> alarmAndStatus) {
        this.alarmAndStatus = alarmAndStatus;
    }

}
