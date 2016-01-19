package c8y.trackeragent.service;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Battery;
import c8y.SignalStrength;
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

    public MeasurementRepresentation createBatteryLevelMeasurement(BigDecimal batteryLevel, TrackerDevice device, DateTime date) {
        MeasurementRepresentation measurement = new MeasurementRepresentation();
        Battery batteryFragment = new Battery();
        batteryFragment.setLevel(measurementValue(batteryLevel, "%"));
        measurement.set(batteryFragment);
        measurement.setType("c8y_Battery");
        measurement.setSource(asSource(device));
        measurement.setTime(date.toDate());
        device.createMeasurement(measurement);
        return measurement;
    }

    public MeasurementRepresentation createGSMLevelMeasurement(BigDecimal gsmLevel, TrackerDevice device, DateTime date) {
        MeasurementRepresentation measurement = new MeasurementRepresentation();
        SignalStrength signalFragment = new SignalStrength();
        signalFragment.setProperty("quality", measurementValue(gsmLevel, "%"));
        measurement.set(signalFragment);
        measurement.setType("c8y_Battery");
        measurement.setSource(asSource(device));
        measurement.setTime(date.toDate());
        device.createMeasurement(measurement);
        return measurement;
    }
    
    private static MeasurementValue measurementValue(BigDecimal value, String unit) {
        MeasurementValue measurementValue = new MeasurementValue();
        measurementValue.setValue(value);
        measurementValue.setUnit(unit);
        return measurementValue;
    }


}
