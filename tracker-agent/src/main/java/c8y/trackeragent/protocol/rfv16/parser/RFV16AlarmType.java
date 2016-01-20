package c8y.trackeragent.protocol.rfv16.parser;

import java.math.BigDecimal;

import c8y.SpeedMeasurement;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.service.AlarmType;
import c8y.trackeragent.service.MeasurementService;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

public enum RFV16AlarmType implements AlarmType {

    NOISE_SENSOR(0, 0) {
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            return EMPTY_ARGS;
        }

    },
    DOOR(2, 0) {
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            return EMPTY_ARGS;
        }
    },
    THEFT(3, 0) {
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            return EMPTY_ARGS;
        }
    },
    LOW_BATTERY(1, 1) {
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            return EMPTY_ARGS;
        }
    },
    SOS(3, 1) {
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            return EMPTY_ARGS;
        }
    },
    // OTHER(1, 2),
    OVERSPEED(3, 2) {
        
        private String formatSpeed(SpeedMeasurement speedFragment) {
            if (speedFragment == null) {
                return "";
            }
            return String.format("%s%s", speedFragment.getSpeed().getValue(), speedFragment.getSpeed().getUnit());
        }
        
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            BigDecimal speedValue = RFV16Parser.getSpeed(reportContext);
            SpeedMeasurement speedFragment = MeasurementService.createSpeedFragment(speedValue);
            return new Object[] { formatSpeed(speedFragment) };
        }
        
    },
    CHARGER_REMOVED(0, 4) {
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            return EMPTY_ARGS;
        }
    },
    ENTER_FENCE_AREA(3, 4) {
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            return EMPTY_ARGS;
        }
    },
    OUT_OF_FENCE(3, 7) {
        @Override
        public Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext) {
            return EMPTY_ARGS;
        }
    };

    private static final Object[] EMPTY_ARGS = new Object[] {};

    private final int byteNo;
    private final int bitNo;

    private RFV16AlarmType(int byteNo, int bitNo) {
        this.byteNo = byteNo;
        this.bitNo = bitNo;
    }

    public int getByteNo() {
        return byteNo;
    }

    public int getBitNo() {
        return bitNo;
    }

}
