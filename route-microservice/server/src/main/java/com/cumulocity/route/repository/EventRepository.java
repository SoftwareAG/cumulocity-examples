package com.cumulocity.route.repository;

import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.event.EventCollection;
import com.cumulocity.sdk.client.event.EventFilter;
import com.cumulocity.route.model.core.CreateEvent;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventRepository {

    private final EventApi events;
    private final MicroserviceSubscriptionsService subscriptions;

    public Iterable<EventRepresentation> findAll(DateTime from, DateTime to) {
        return subscriptions.callForTenant(subscriptions.getTenant(), () -> {
            final EventFilter filter = new EventFilter().byDate(from.toDate(), to.toDate());
            final EventCollection measurementsByFilter = events.getEventsByFilter(filter);
            return measurementsByFilter.get(1000).allPages();
        });
    }

    public void sendEvent(MicroserviceCredentials credentials, CreateEvent o) {
        subscriptions.runForTenant(credentials.getTenant(), () -> {
            final ManagedObjectRepresentation source = new ManagedObjectRepresentation();
            source.setId(GId.asGId(o.getSource()));

            final EventRepresentation event = new EventRepresentation();
            event.setDateTime(o.getTime());
            event.setType(o.getType());
            event.setText(o.getType());
            event.setSource(source);
            events.create(event);
        });
    }
}
