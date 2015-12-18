package c8y.trackeragent.protocol.coban.parser;

import c8y.SpeedMeasurement;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.protocol.coban.service.MeasurementService;

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
            alarm.setText("Batteriezustand ist kritisch.");
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
            alarm.setText("Bewegungsalarm.");
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
            alarm.setText("Erschütterungsalarm.");
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
            SpeedMeasurement speedFragment = MeasurementService.createSpeedFragment(reportContext);
            String text = String.format("Geschwindigkeitsüberschreitung %s", formatSpeed(speedFragment));
            alarm.setType(asC8yType());
            alarm.setText(text);
            alarm.setSeverity(CumulocitySeverities.MAJOR.toString());
        }

        private String formatSpeed(SpeedMeasurement speedFragment) {
            if (speedFragment == null) {
                return "";
            }
            return String.format("%s%s", speedFragment.getSpeed().getValue(), speedFragment.getSpeed().getUnit());
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
    },
    
    POWER_OFF {
        
        @Override
        public String asC8yType() {
            return "c8y_PowerAlarm";
        }
        
        @Override
        public String asCobanType() {
            return "ac alarm";
        }
        
        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }
        
        @Override
        public void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext) {
            alarm.setType(asC8yType());
            alarm.setText("Device lost power");
            alarm.setSeverity(CumulocitySeverities.MAJOR.toString());
        }
    },
    
    NO_GPS_SIGNAL {
        
        @Override
        public String asC8yType() {
            return "c8y_NoGPSSignal";
        }
        
        @Override
        public String asCobanType() {
            return NO_COBAN_TYPE;
        }
        
        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }
        
        @Override
        public void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext) {
            alarm.setType(asC8yType());
            alarm.setText("kein GPS-Signal");
            alarm.setSeverity(CumulocitySeverities.CRITICAL.toString());
        }
    },
    
    SOS {
        
        @Override
        public String asC8yType() {
            return "c8y_SOS";
        }

        @Override
        public String asCobanType() {
            return "help me";
        }

        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }
        
        @Override
        public void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext) {
            alarm.setType(asC8yType());
            alarm.setText("NOTRUF");
            alarm.setSeverity(CumulocitySeverities.MAJOR.toString());
        }
    };
    
    private static final String NO_COBAN_TYPE = "___";

    public abstract String asC8yType();
    
    public abstract String asCobanType();
    
    public abstract boolean accept(String[] report);
    
    public abstract void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext);
    
    private static boolean accept1(AlarmType alarmType, String[] report) {
        return alarmType.asCobanType().equals(report[1]);
    }
        
}
