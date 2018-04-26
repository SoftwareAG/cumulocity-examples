package com.cumulocity.route.service;

import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.route.model.TriggerPause;
import com.cumulocity.route.model.core.CreateEvent;
import com.cumulocity.route.repository.EventRepository;
import com.cumulocity.route.repository.ManagedObjectRepository;
import com.cumulocity.route.service.engine.StateMachine;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicReference;

import static com.cumulocity.model.idtype.GId.asGId;
import static com.cumulocity.route.service.RouteTestUtils.*;
import static com.cumulocity.route.service.engine.From.every;
import static org.assertj.core.api.Assertions.assertThat;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@Slf4j(topic = "machine")
@RunWith(MockitoJUnitRunner.class)
public class RoutesServiceTest {

    @Mock
    private ManagedObjectRepository managedObjects;

    @Mock
    private EventRepository events;

    @Spy
    private StateMachine stateMachine;

    @InjectMocks
    private RouteService routes;

    @Before
    public void before() {
        DateTimeUtils.setCurrentMillisFixed(123456);
        routes.onMicroserviceSubscribed(subscriptionEvent());
        stateMachine.handle(every(Object.class).transform(Object::toString).consume(log::debug));
    }

    @Test
    public void shouldPauseAfterFirstSpeedEvent() {
        final AtomicReference<TriggerPause> event = new AtomicReference<>();
        stateMachine.handle(every(TriggerPause.class).consume(event::set));

        routes.onMeasurementCreated(speedEvent("123", now(), 2));

        assertThat(event.get()).isEqualTo(new TriggerPause(measurement("123", now(),2)));
        verify(events, never()).sendEvent(any(MicroserviceCredentials.class), any(CreateEvent.class));
    }

    @Test
    public void shouldSendStartEventForSecondSpeedEvent() {
        routes.onMeasurementCreated(speedEvent("123", now(), 2));
        routes.onMeasurementCreated(speedEvent("123", now(), 3));

        verify(events).sendEvent(any(MicroserviceCredentials.class), eq(new CreateEvent(
                asGId("123"), "c8y_RouteStart", now().minusSeconds(25)
        )));
        verifyNoMoreInteractions(events);
    }

    @Test
    public void shouldSendStopEventForMovementStopEvent() {
        routes.onMeasurementCreated(speedEvent("123", now(), 2));
        routes.onMeasurementCreated(speedEvent("123", now(), 3));
        routes.onEventCreated(stopEvent("123"));

        verify(events).sendEvent(any(MicroserviceCredentials.class), eq(new CreateEvent(
                asGId("123"), "c8y_RouteStart", now().minusSeconds(25)
        )));
        verify(events).sendEvent(any(MicroserviceCredentials.class), eq(new CreateEvent(
                asGId("123"), "c8y_RouteEnd", now().plusSeconds(10)
        )));
        verifyNoMoreInteractions(events);
    }

    @Test
    public void shouldSendResumeMovementEventForThirdSpeedEvent() {
        routes.onMeasurementCreated(speedEvent("123", now(), 2));
        routes.onMeasurementCreated(speedEvent("123", now(), 3));
        routes.onEventCreated(stopEvent("123"));
        routes.onMeasurementCreated(speedEvent("123", now().plusSeconds(1), 4));

        verify(events).sendEvent(any(MicroserviceCredentials.class), eq(new CreateEvent(
                asGId("123"), "c8y_RouteStart", now().minusSeconds(25)
        )));
        verify(events).sendEvent(any(MicroserviceCredentials.class), eq(new CreateEvent(
                asGId("123"), "c8y_RouteEnd", now().plusSeconds(10)
        )));
        verify(events).sendEvent(any(MicroserviceCredentials.class), eq(new CreateEvent(
                asGId("123"), "c8y_RouteStart", now().minusSeconds(24)
        )));
        verifyNoMoreInteractions(events);
    }

    @Test
    public void shouldNotGenerateStopEventForStopEventInDifferentDevice() {
        routes.onMeasurementCreated(speedEvent("123", now(), 2));
        routes.onMeasurementCreated(speedEvent("123", now(), 3));
        routes.onEventCreated(stopEvent("4564646"));

        verify(events).sendEvent(any(MicroserviceCredentials.class), eq(new CreateEvent(
                asGId("123"), "c8y_RouteStart", now().minusSeconds(25)
        )));
        verifyNoMoreInteractions(events);
    }
}
