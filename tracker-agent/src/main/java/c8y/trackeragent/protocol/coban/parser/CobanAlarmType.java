package c8y.trackeragent.protocol.coban.parser;

import java.math.BigDecimal;

import c8y.SpeedMeasurement;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.service.AlarmType;
import c8y.trackeragent.service.MeasurementService;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

public enum CobanAlarmType implements AlarmType {
    
    LOW_BATTERY {
        
        @Override
        public String asCobanType() {
            return "low battery";
        }

        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }
        
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            return EMPTY_ARGS;
        }
    },
    
    MOVE {

        @Override
        public String asCobanType() {
            return "move";
        }

        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }

        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            return EMPTY_ARGS;
        }
    },
    
    SHOCK {
        
        @Override
        public String asCobanType() {
            return "sensor alarm";
        }
        
        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }
        
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            return EMPTY_ARGS;
        }
    },
    
    OVERSPEED {
        
        @Override
        public String asCobanType() {
            return "speed";
        }
        
        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }
        
        private String formatSpeed(SpeedMeasurement speedFragment) {
            if (speedFragment == null) {
                return "";
            }
            return String.format("%s%s", speedFragment.getSpeed().getValue(), speedFragment.getSpeed().getUnit());
        }
        
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            BigDecimal speedValue = CobanParser.getSpeed(reportContext);
            SpeedMeasurement speedFragment = MeasurementService.createSpeedFragment(speedValue);
            return new Object[]{formatSpeed(speedFragment)};
        }
    },
    
    OUT_OF_FENCE {
        
        @Override
        public String asCobanType() {
            return "stockade";
        }
        
        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }
        
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            return EMPTY_ARGS;
        }
    },
    
    POWER_OFF {
        
        @Override
        public String asCobanType() {
            return "ac alarm";
        }
        
        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }
        
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            return EMPTY_ARGS;
        }
    },
    
    NO_GPS_SIGNAL {
        
        @Override
        public String asCobanType() {
            return "no_gps_signal";
        }
        
        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }
        
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            return EMPTY_ARGS;
        }
    },
    
    SOS {
        
        @Override
        public String asCobanType() {
            return "help me";
        }

        @Override
        public boolean accept(String[] report) {
            return accept1(this, report);
        }
        
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            return EMPTY_ARGS;
        }
    };
    
    private static final Object[] EMPTY_ARGS = new Object[]{};
    
    public abstract String asCobanType();
    
    public abstract boolean accept(String[] report);
    
    private static boolean accept1(CobanAlarmType alarmType, String[] report) {
        return alarmType.asCobanType().equals(report[1]);
    }
        
}
