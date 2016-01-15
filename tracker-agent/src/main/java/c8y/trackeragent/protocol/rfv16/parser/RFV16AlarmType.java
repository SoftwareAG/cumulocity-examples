package c8y.trackeragent.protocol.rfv16.parser;

import java.math.BigDecimal;

import c8y.SpeedMeasurement;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.service.MeasurementService;

import com.cumulocity.model.event.CumulocitySeverities;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

public enum RFV16AlarmType {
    
    //NOISE_SENSOR(0, 0),
//    DOOR(2, 0) {
//    
//        public String asC8yType() {
//            
//        }
//        
//        public void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext) {
//            
//        }
//    },
//    THEFT(3, 0) {
//        
//        public String asC8yType() {
//            
//        }
//        
//        public void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext) {
//            
//        }
//    },
    LOW_BATTERY(1, 1) {
        
        public String asC8yType() {
            return "c8y_LowBattery";
        }
        
        public void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext) {
            alarm.setType(asC8yType());
            alarm.setText("Batteriezustand ist kritisch.");
            alarm.setSeverity(CumulocitySeverities.MAJOR.toString());            
        }
    },
    SOS(3, 1) {
        public String asC8yType() {
            return "c8y_SOS";
        }
        
        public void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext) {
            alarm.setType(asC8yType());
            alarm.setText("NOTRUF");
            alarm.setSeverity(CumulocitySeverities.MAJOR.toString());
        }
        
    },
    //OTHER(1, 2),
    OVERSPEED(3, 2) {
        public String asC8yType() {
            return "c8y_Overspeed";
        }
        
        public void populateAlarm(AlarmRepresentation alarm, ReportContext reportCtx) {
            BigDecimal speedValue = RFV16Parser.getSpeed(reportCtx);
            SpeedMeasurement speedFragment = MeasurementService.createSpeedFragment(speedValue);
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
    };
    //CHARGER_REMOVED(0, 4),
    //ENTER_FENCE_AREA(3, 4),
    //OUT_OF_FENCE(3, 7);
    
    private int byteNo;
    private int bitNo;
    
    private RFV16AlarmType(int byteNo, int bitNo) {
        this.byteNo = byteNo;
        this.bitNo = bitNo;
    }

    public int getByteNo() {
        return byteNo;
    }

    public void setByteNo(int byteNo) {
        this.byteNo = byteNo;
    }

    public int getBitNo() {
        return bitNo;
    }

    public void setBitNo(int bitNo) {
        this.bitNo = bitNo;
    }
    
    public abstract String asC8yType();
    
    public abstract void populateAlarm(AlarmRepresentation alarm, ReportContext reportContext);

}
