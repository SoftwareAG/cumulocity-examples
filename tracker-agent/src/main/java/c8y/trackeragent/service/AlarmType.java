package c8y.trackeragent.service;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

import c8y.trackeragent.context.ReportContext;

public interface AlarmType {
    
    Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext);
    
    String name();

}
