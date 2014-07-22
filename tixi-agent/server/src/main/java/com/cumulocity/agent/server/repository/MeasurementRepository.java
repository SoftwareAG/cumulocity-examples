package com.cumulocity.agent.server.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.measurement.MeasurementApi;

@Component
public class MeasurementRepository {

    private static final Logger log = LoggerFactory.getLogger(MeasurementRepository.class);

    private final MeasurementApi measurementApi;

    @Autowired
    public MeasurementRepository(MeasurementApi measurementApi) {
        this.measurementApi = measurementApi;
    }

    public MeasurementRepresentation save(MeasurementRepresentation representation) {
        log.debug("Save measurment : {}.", representation);
        if (representation.getId() == null) {
            return measurementApi.create(representation);
        } else {
            throw new UnsupportedOperationException("Unable to update measurment");
        }
    }
}
