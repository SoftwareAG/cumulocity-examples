package com.cumulocity.greenbox.server.service;

import static java.math.BigDecimal.valueOf;

import org.springframework.stereotype.Component;

import com.cumulocity.model.measurement.MeasurementValue;

@Component
public class MilliVoltsToVoltsMeasurementPostProcessor implements MeasurementValuePostProcessor {

    private static final String VOLTS = "V";

    private static final String MILLI_VOLTS = "mV";

    @Override
    public MeasurementValue process(MeasurementValue measurement) {
        if (isMilliVolts(measurement)) {
            return toVolts(measurement);
        }
        return measurement;
    }

    private MeasurementValue toVolts(MeasurementValue measurement) {
        return new MeasurementValue(measurement.getValue().divide(valueOf(1000)), VOLTS, measurement.getType(), measurement.getQuantity(),
                measurement.getState());
    }

    private boolean isMilliVolts(MeasurementValue measurement) {
        return MILLI_VOLTS.equals(measurement.getUnit());
    }
}
