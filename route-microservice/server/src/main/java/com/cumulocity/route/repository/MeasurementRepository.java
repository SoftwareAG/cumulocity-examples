package com.cumulocity.route.repository;

import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.cumulocity.sdk.client.measurement.MeasurementCollection;
import com.cumulocity.sdk.client.measurement.MeasurementFilter;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MeasurementRepository {
    private final MeasurementApi measurementApi;
    private final MicroserviceSubscriptionsService subscriptions;

    public Iterable<MeasurementRepresentation> findAll(DateTime from, DateTime to) {
        return subscriptions.callForTenant(subscriptions.getTenant(), () -> {
            final MeasurementFilter filter = new MeasurementFilter().byDate(from.toDate(), to.toDate());
            final MeasurementCollection measurementsByFilter = measurementApi.getMeasurementsByFilter(filter);
            return measurementsByFilter.get(1000).allPages();
        });
    }
}
