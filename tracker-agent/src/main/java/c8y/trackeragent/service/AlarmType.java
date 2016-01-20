package c8y.trackeragent.service;

import c8y.trackeragent.ReportContext;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

public interface AlarmType {
    
    Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext);
    
    String name();

}
