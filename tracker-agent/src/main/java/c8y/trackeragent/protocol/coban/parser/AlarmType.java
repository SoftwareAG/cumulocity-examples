package c8y.trackeragent.protocol.coban.parser;

import c8y.trackeragent.ReportContext;

import com.cumulocity.model.event.CumulocitySeverities;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

public enum AlarmType {
    
    LOW_BATTERY {
        
        @Override
        public String asKeyword() {
            return "low battery";
        }

        @Override
        public boolean accept(String[] report) {
            return asKeyword().equals(report[1]);
        }
        
        @Override
        public void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext) {
            alarm.setType("c8y_LowBattery");
            alarm.setSeverity(CumulocitySeverities.MAJOR.toString());
        }
        
    };
    
    public abstract String asKeyword();
    
    public abstract boolean accept(String[] report);
    
    public abstract void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext);
        
}
