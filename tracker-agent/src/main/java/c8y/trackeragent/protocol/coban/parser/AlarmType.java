package c8y.trackeragent.protocol.coban.parser;

import c8y.trackeragent.ReportContext;

import com.cumulocity.model.event.CumulocitySeverities;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

public enum AlarmType {
    
    LOW_BATTERY {
        
        @Override
        public String asC8yType() {
            return "c8y_LowBattery";
        }

        @Override
        public String asCobanType() {
            return "low battery";
        }

        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }
        
        @Override
        public void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext) {
            alarm.setType(asC8yType());
            alarm.setText("Battery level is low.");
            alarm.setSeverity(CumulocitySeverities.MAJOR.toString());
        }
    },
    
    MOVE {

        @Override
        public String asC8yType() {
            return "c8y_Move";
        }

        @Override
        public String asCobanType() {
            return "move";
        }

        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }

        @Override
        public void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext) {
            alarm.setType(asC8yType());
            alarm.setText("Device moved.");
            alarm.setSeverity(CumulocitySeverities.MAJOR.toString());
        }
    },
    
    SHOCK {
        
        @Override
        public String asC8yType() {
            return "c8y_Shock";
        }
        
        @Override
        public String asCobanType() {
            return "sensor alarm";
        }
        
        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }
        
        @Override
        public void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext) {
            alarm.setType(asC8yType());
            alarm.setText("Device shocked.");
            alarm.setSeverity(CumulocitySeverities.MAJOR.toString());
        }
    },
    
    OVERSPEED {
        
        @Override
        public String asC8yType() {
            return "c8y_Overspeed";
        }
        
        @Override
        public String asCobanType() {
            return "speed";
        }
        
        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }
        
        @Override
        public void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext) {
            alarm.setType(asC8yType());
            alarm.setText("Device over speed.");
            alarm.setSeverity(CumulocitySeverities.MAJOR.toString());
        }
    },
    
    GEOFENCE {
        
        @Override
        public String asC8yType() {
            return "c8y_Geofence";
        }
        
        @Override
        public String asCobanType() {
            return "stockade";
        }
        
        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }
        
        @Override
        public void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext) {
            alarm.setType(asC8yType());
            alarm.setText("Device out of geofence.");
            alarm.setSeverity(CumulocitySeverities.MAJOR.toString());
        }
    };
    
    public abstract String asC8yType();
    
    public abstract String asCobanType();
    
    public abstract boolean accept(String[] report);
    
    public abstract void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext);
    
    private static boolean accept1(AlarmType alarmType, String[] report) {
        return alarmType.asCobanType().equals(report[1]);
    }
        
}
