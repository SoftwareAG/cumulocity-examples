package c8y.trackeragent.service;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.SpeedMeasurement;
import c8y.trackeragent.TrackerDevice;

import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;

public class MeasurementService {
    
    private static Logger logger = LoggerFactory.getLogger(MeasurementService.class);
    
    public SpeedMeasurement createSpeedMeasurement(BigDecimal speedValue, TrackerDevice device, DateTime date) {
        SpeedMeasurement speedFragment = createSpeedFragment(speedValue);
        if (speedFragment == null) {
            return null;
        }
        MeasurementRepresentation measurement = asMeasurement(device, speedFragment, date);
        logger.debug("Create speed measurement: ", measurement);
        device.createMeasurement(measurement);
        return speedFragment;
    }
    
    public SpeedMeasurement createSpeedMeasurement(BigDecimal speedValue, TrackerDevice device) {
        return createSpeedMeasurement(speedValue, device, new DateTime());
    }
    
    public static SpeedMeasurement createSpeedFragment(BigDecimal speedValue) {
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

    private MeasurementRepresentation asMeasurement(TrackerDevice device, SpeedMeasurement speedFragment, DateTime date) {
        MeasurementRepresentation measurement = new MeasurementRepresentation();
        measurement.set(speedFragment);
        measurement.setType("c8y_Speed");
        measurement.setSource(asSource(device));
        measurement.setTime(date.toDate());
        return measurement;
    }

    private ManagedObjectRepresentation asSource(TrackerDevice device) {
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(device.getGId());
        return source;
    }


}
