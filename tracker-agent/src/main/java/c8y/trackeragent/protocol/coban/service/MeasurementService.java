package c8y.trackeragent.protocol.coban.service;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.SpeedMeasurement;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerDevice;

import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;

public class MeasurementService {
    
    private static Logger logger = LoggerFactory.getLogger(AlarmService.class);
    
    public SpeedMeasurement createSpeedMeasurement(ReportContext reportCtx, TrackerDevice device) {
        SpeedMeasurement speedFragment = createSpeedFragment(reportCtx);
        if (speedFragment == null) {
            return null;
        }
        MeasurementRepresentation measurement = asMeasurement(device, speedFragment);
        logger.debug("Create speed measurement: ", measurement);
        device.createMeasurement(measurement);
        return speedFragment;
    }
    
    public static SpeedMeasurement createSpeedFragment(ReportContext reportCtx) {
        BigDecimal speedValue = getSpeed(reportCtx);
        if (speedValue == null) {
            return null;
        }
        SpeedMeasurement speedFragment = new SpeedMeasurement();
        MeasurementValue speed = new MeasurementValue();
        speedFragment.setSpeed(speed);
        speed.setUnit("km/h");
        speed.setValue(speedValue);
        return speedFragment;
    }

    private MeasurementRepresentation asMeasurement(TrackerDevice device, SpeedMeasurement speedFragment) {
        MeasurementRepresentation measurement = new MeasurementRepresentation();
        measurement.set(speedFragment);
        measurement.setType("c8y_Speed");
        measurement.setSource(asSource(device));
        measurement.setTime(new Date());
        return measurement;
    }

    private ManagedObjectRepresentation asSource(TrackerDevice device) {
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(device.getGId());
        return source;
    }

    public static BigDecimal getSpeed(ReportContext reportCtx) {
        String entry = reportCtx.getEntry(12);
        if (entry == null) {
            logger.warn("There is no speed parameter in measurement");
            return null;
        }
        try {
            return new BigDecimal(entry);
        } catch (NumberFormatException nfex) {
            logger.error("Wrong speed value: " + entry, nfex);
            return null;
        }
    }


}
