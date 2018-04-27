package com.cumulocity.route.service;

import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.route.model.core.EventCreated;
import com.cumulocity.route.model.core.MeasurementCreated;
import lombok.experimental.UtilityClass;
import org.joda.time.DateTime;

import static com.google.common.collect.ImmutableMap.of;

@UtilityClass
public class RouteTestUtils {
    public static EventCreated stopEvent(String device) {
        final EventRepresentation event = new EventRepresentation();
        event.setType("my_bike_movement_stop");
        event.setSource(managedObject(device));
        return new EventCreated(event);
    }

    public static MeasurementCreated speedEvent(String device, DateTime time, double value) {
        return new MeasurementCreated(measurement(device, time, value));
    }

    public static MeasurementRepresentation measurement(String sourceId, DateTime time, double value) {
        final ManagedObjectRepresentation source = managedObject(sourceId);

        final MeasurementRepresentation measurement = new MeasurementRepresentation();
        measurement.setProperty("GPS_Speed", of(
                "V", of(
                        "value", value
                )
        ));
        measurement.setDateTime(time);
        measurement.setSource(source);
        return measurement;
    }

    public static ManagedObjectRepresentation managedObject(String sourceId) {
        final ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(sourceId));
        return source;
    }

    public static MicroserviceSubscriptionAddedEvent subscriptionEvent() {
        return new MicroserviceSubscriptionAddedEvent(new MicroserviceCredentials());
    }
}
