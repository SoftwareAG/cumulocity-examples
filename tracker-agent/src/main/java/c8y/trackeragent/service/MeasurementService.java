package c8y.trackeragent.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;

import c8y.Battery;
import c8y.SignalStrength;
import c8y.SpeedMeasurement;
import c8y.trackeragent.TrackerDevice;

@Component
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
    
    public static Map<String, Object> createAltitudeFragment(BigDecimal altitude) {
        if (altitude == null) {
            return null;
        }
        Map<String, Object> altFragment = new HashMap<String, Object>();
        MeasurementValue alt = new MeasurementValue();
        altFragment.put("altitude", alt);
        alt.setUnit("m");
        alt.setValue(altitude);
        return altFragment;
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
    
    public MeasurementRepresentation createPercentageBatteryLevelMeasurement(BigDecimal percentageBatteryLevel, TrackerDevice device, DateTime date) {
        return createBatteryLevelMeasurement(percentageBatteryLevel, device, date, "%");
    }
    
    public MeasurementRepresentation createBatteryLevelMeasurement(BigDecimal batteryLevel, TrackerDevice device, DateTime date, String unit) {
        // TODO align with TrackerDevice
        MeasurementRepresentation measurement = new MeasurementRepresentation();
        Battery batteryFragment = new Battery();
        batteryFragment.setLevel(measurementValue(batteryLevel, unit));
        measurement.set(batteryFragment, "c8y_TrackerBattery");
        measurement.setType("c8y_TrackerBattery");
        measurement.setSource(asSource(device));
        measurement.setTime(date.toDate());
        device.createMeasurement(measurement);
        return measurement;
    }

    public MeasurementRepresentation createGSMLevelMeasurement(BigDecimal percentageGSMLevel, TrackerDevice device, DateTime date) {
        // TODO align with TrackerDevice
        MeasurementRepresentation measurement = new MeasurementRepresentation();
        SignalStrength signalFragment = new SignalStrength();
        signalFragment.setProperty("quality", measurementValue(percentageGSMLevel, "%"));
        measurement.set(signalFragment,"c8y_SignalStrength");
        measurement.setType("c8y_SignalStrength");
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

    public Map<String, Object> createAltitudeMeasurement(BigDecimal altitude, TrackerDevice device, DateTime date) {
        Map<String, Object> altFragment = createAltitudeFragment(altitude);
        if (altFragment == null) {
            return null;
        }
        MeasurementRepresentation measurement = new MeasurementRepresentation();
        measurement.set(altFragment, "c8y_Altitude");
        measurement.setType("c8y_Altitude");
        measurement.setSource(asSource(device));
        measurement.setTime(date.toDate());
        logger.debug("Create altitude measurement: ", measurement);
        device.createMeasurement(measurement);
        return altFragment;
    }
    
    public void createMileageMeasurement(BigDecimal mileage, TrackerDevice device, DateTime date) {
        MeasurementRepresentation measurement = asMeasurementWithMileage(mileage, device, date);
        device.createMeasurement(measurement);
    }

    private MeasurementRepresentation asMeasurementWithMileage(BigDecimal mileage, TrackerDevice device, DateTime date) {
        MeasurementRepresentation representation = new MeasurementRepresentation();
        representation.setTime(date.toDate());
        representation.setSource(asSource(device));
        representation.setType("c8y_TrackerMileage");
        Map<String, Object> measurementValue = new HashMap<String, Object>();
        measurementValue.put("value", mileage);
        measurementValue.put("unit", "km");

        Map<String, Object> measurementSerie = new HashMap<String, Object>();
        measurementSerie.put("c8y_DistanceMeasurement", measurementValue);

        representation.set(measurementSerie, "c8y_TrackerMileage");

        return representation;
    }
    
    public void createMotionMeasurement(boolean motion, TrackerDevice device, DateTime date) {
        MeasurementRepresentation measurement = asMeasurementWithMotion(motion, device, date);
        device.createMeasurement(measurement);
    }
    
    private MeasurementRepresentation asMeasurementWithMotion(boolean motion, TrackerDevice device, DateTime date) {
        MeasurementRepresentation representation = new MeasurementRepresentation();
        representation.setTime(date.toDate());
        representation.setSource(asSource(device));
        representation.setType("c8y_TrackerMotion");
        MeasurementValue measurementValue = new MeasurementValue();
        measurementValue.setValue(motion ? BigDecimal.ONE : BigDecimal.ZERO);
        
        Map<String, Object> measurementSerie = new HashMap<String, Object>();
        measurementSerie.put("c8y_TrackerMotion", measurementValue);
        
        representation.set(measurementSerie, "c8y_TrackerMotion");
        
        return representation;
    }
}
