package com.cumulocity.snmp.integration.platform.service;

import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MeasurementMockService implements Closeable {

    @Data
    public static class MeasurementAdded {
        private final MeasurementRepresentation measurement;
    }

    private List<MeasurementRepresentation> store = new CopyOnWriteArrayList<>();

    @Autowired
    private ApplicationEventPublisher publisher;

    public MeasurementRepresentation store(MeasurementRepresentation measurement) {
        store.add(measurement);
        publisher.publishEvent(new MeasurementAdded(measurement));
        return measurement;
    }

    public List<MeasurementRepresentation> getAllMeasurements() {
        return store;
    }

    @Override
    public void close() {
        clear();
    }

    public void clear() {
        store.clear();
    }

}
