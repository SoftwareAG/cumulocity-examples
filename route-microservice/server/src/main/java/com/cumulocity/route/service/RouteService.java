package com.cumulocity.route.service;

import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.route.model.*;
import com.cumulocity.route.model.core.*;
import com.cumulocity.route.repository.EventRepository;
import com.cumulocity.route.repository.IdentityRepository;
import com.cumulocity.route.repository.ManagedObjectRepository;
import com.cumulocity.route.repository.MeasurementRepository;
import com.cumulocity.route.service.engine.StateMachine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.cumulocity.route.repository.IdentityRepository.MICROSERVICE_ID_TYPE;
import static com.cumulocity.route.service.engine.From.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

    public static final String MICROSERVICE_ID = "Route";
    private static final String SPEED_MEASUREMENT_FRAGMENT = "GPS_Speed";
    private static final String SPEED_MEASUREMENT_SERIES = "V";
    private static final int SPEED_MEASUREMENT_THRESHOLD = 1;

    private static final String EVENT_ROUTE_END_TYPE = "c8y_RouteEnd";
    private static final String EVENT_ROUTE_START_TYPE = "c8y_RouteStart";
    private static final String EVENT_STOP_TYPE = "my_bike_movement_stop";

    private final StateMachine stateMachine;
    private final EventRepository events;
    private final MeasurementRepository measurements;
    private final ManagedObjectRepository inventory;
    private final MicroserviceSubscriptionsService subscriptions;
    private final IdentityRepository identity;

    @Scheduled(fixedDelay = 10 * 1000)
    public void scheduler() {
        final DateTime now = DateTime.now();

        subscriptions.runForEachTenant(() -> {
            try {
                final ManagedObjectRepresentation source = findOrCreateSource(subscriptions.getTenant());

                try {
                    final DateTime lastTimeRunning = getLastTimeRunning(source);

                    measurements.findAll(lastTimeRunning, now).forEach(o -> {
                        try {
                            onMeasurementCreated(new MeasurementCreated(o));
                        } catch (final Exception ex) {
                            log.error(ex.getMessage(), ex);
                        }
                    });
                    events.findAll(lastTimeRunning, now).forEach(o -> {
                        try {
                            onEventCreated(new EventCreated(o));
                        } catch (final Exception ex) {
                            log.error(ex.getMessage(), ex);
                        }
                    });
                } finally {
                    saveLastTimeRunning(source, now);
                }
            } catch (final Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        });
    }

    public void onEventCreated(EventCreated event) {
        stateMachine.send(event);
    }

    public void onMeasurementCreated(MeasurementCreated event) {
        stateMachine.send(event);
    }

    @EventListener
    public void onMicroserviceSubscribed(MicroserviceSubscriptionAddedEvent event) {
        final MicroserviceCredentials credentials = event.getCredentials();
        final String tenant = credentials.getTenant();

        stateMachine.handle(every(Object.class).transform(Object::toString).consume(log::trace));

//        context of events is bound do their source device
        stateMachine.contextPartition(
                every(HasSource.class).transform(HasSource::getSource)
        );

//        every CreateEvent -> send to platform
        stateMachine.handle(
                every(CreateEvent.class).consume(o -> events.sendEvent(credentials, o))
        );

//        every MeasurementCreated("GPS_Speed") -> SpeedMeasurement
        stateMachine.insert(
                every(MeasurementCreated.class)
                .filter(o -> o.existsFragment(SPEED_MEASUREMENT_FRAGMENT))
                .transform(o -> new SpeedMeasurement(
                    o.getMeasurement(),
                    o.getNumber(SPEED_MEASUREMENT_FRAGMENT, SPEED_MEASUREMENT_SERIES)
                ))
        );

//        first SpeedMeasurement -> TriggerPause
        stateMachine.insert(
                first(SpeedMeasurement.class).transform(TriggerPause::new)
        );

//        every (SpeedMeasurement, EventCreated("my_bike_movement_stop")) -> TriggerPause
        stateMachine.insert(
                every(
                    begin(SpeedMeasurement.class).filter(o -> o.valueIsGreaterThan(SPEED_MEASUREMENT_THRESHOLD)),
                    end(EventCreated.class).filter(o -> o.eventTypeEquals(EVENT_STOP_TYPE))
                )
                .last(SpeedMeasurement.class)
                .transform(TriggerPause::new)
        );

//        every (TriggerPause, SpeedMeasurement) -> TriggerTrip
        stateMachine.insert(
                every(
                    begin(TriggerPause.class),
                    end(SpeedMeasurement.class).filter(o -> o.valueIsGreaterThan(SPEED_MEASUREMENT_THRESHOLD))
                )
                .transform(Section::getEnd)
                .transform(TriggerTrip::new)
        );

//        every (TriggerPause, TriggerTrip) -> StartTrip
        stateMachine.insert(
                every(
                    begin(TriggerPause.class),
                    end(TriggerTrip.class)
                )
                .transform(Section::getEnd)
                .transform(o -> new StartTrip(
                        o.getMeasurement(),
                        inventory.findManagedObjectById(tenant, o.getSource()))
                )
        );

//        every (TriggerTrip, TriggerPause) -> StartPause
        stateMachine.insert(
                every(
                    begin(TriggerTrip.class),
                    end(TriggerPause.class)
                )
                .transform(Section::getEnd)
                .transform(o -> new StartPause(
                        o.getMeasurement(),
                        inventory.findManagedObjectById(tenant, o.getSource())
                ))
        );

//        every StartTrip -> CreateEvent("c8y_RouteStart")
        stateMachine.insert(
                every(StartTrip.class)
                .transform(o -> new CreateEvent(
                        o.getSource(),
                        EVENT_ROUTE_START_TYPE,
                        o.getMeasurement().getDateTime().minusSeconds(25)
                ))
        );

//        every StartPause -> CreateEvent("c8y_RouteEnd")
        stateMachine.insert(
                every(StartPause.class)
                .transform(o -> new CreateEvent(
                        o.getSource(),
                        EVENT_ROUTE_END_TYPE,
                        o.getMeasurement().getDateTime().plusSeconds(10)
                ))
        );
    }

    private DateTime getLastTimeRunning(ManagedObjectRepresentation source) {
        final Object lastTimeRunning = source.get("lastTimeRunning");
        if (lastTimeRunning == null) {
            return DateTime.now();
        } else {
            return ISODateTimeFormat.dateTimeParser().withOffsetParsed().parseDateTime(String.valueOf(lastTimeRunning));
        }
    }

    private void saveLastTimeRunning(ManagedObjectRepresentation source, DateTime now) {
        source.setLastUpdatedDateTime(null);
        source.setProperty("lastTimeRunning", ISODateTimeFormat.dateTime().print(now));
        inventory.update(source);
    }

    private ManagedObjectRepresentation findOrCreateSource(String tenant) {
        return identity.find(MICROSERVICE_ID_TYPE, MICROSERVICE_ID)
                .transform(gId -> inventory.findManagedObjectById(tenant, gId))
                .or(() -> {
                    final ManagedObjectRepresentation source = inventory.create(MICROSERVICE_ID_TYPE, MICROSERVICE_ID);
                    identity.create(source.getId(), MICROSERVICE_ID_TYPE, MICROSERVICE_ID);
                    return source;
                });
    }
}
