package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.GatewayManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.pubsub.service.EventPubSub;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.agent.snmp.platform.service.PlatformProvider;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.event.EventApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class EventSubscriberTest {

    @Mock
    private GatewayProperties gatewayProperties;

    @Mock
    private GatewayDataProvider gatewayDataProvider;

    @Mock
    private EventPubSub eventPubSub;

    @Mock
    private EventApi eventApi;

    @Mock
    private GatewayManagedObjectWrapper gatewayManagedObjectWrapper;

    @Mock
    private GatewayManagedObjectWrapper.SnmpCommunicationProperties snmpCommunicationProperties;

    @Mock
    private PlatformProvider platformProvider;

    @InjectMocks
    private EventSubscriber eventSubscriber;


    @Test
    public void shouldGetBatchingSupportedAsFalse() {
        assertFalse(eventSubscriber.isBatchingSupported());
    }

    @Test
    public void shouldGetDefaultBatchSize() {
        assertEquals(200, eventSubscriber.getBatchSize());
    }

    @Test
    public void shouldGetConcurrentSubscriptionsCountAs_10Percent_OfSchedulerPoolSize_1() {

        int gatewayThreadPoolSize = 1;

        Mockito.when(gatewayProperties.getGatewayThreadPoolSize()).thenReturn(gatewayThreadPoolSize);

        assertEquals(1, eventSubscriber.getConcurrentSubscriptionsCount());
    }

    @Test
    public void shouldGetConcurrentSubscriptionsCountAs_10Percent_OfSchedulerPoolSize_10() {

        int gatewayThreadPoolSize = 10;

        Mockito.when(gatewayProperties.getGatewayThreadPoolSize()).thenReturn(gatewayThreadPoolSize);

        assertEquals((gatewayThreadPoolSize * 10/100), eventSubscriber.getConcurrentSubscriptionsCount());
    }

    @Test
    public void shouldGetConcurrentSubscriptionsCountAs_10Percent_OfSchedulerPoolSize_101() {

        int schedularPoolSize = 101;

        Mockito.when(gatewayProperties.getGatewayThreadPoolSize()).thenReturn(schedularPoolSize);

        assertEquals((schedularPoolSize * 10/100), eventSubscriber.getConcurrentSubscriptionsCount());
    }

    @Test
    public void shouldHandleMessageSuccessfully() {
        ArgumentCaptor<EventSubscriber.EventRepresentation> eventRepresentationCaptor = ArgumentCaptor.forClass(EventSubscriber.EventRepresentation.class);

        Mockito.when(eventApi.create(Mockito.any(EventSubscriber.EventRepresentation.class))).thenReturn(null);

        eventSubscriber.handleMessage("SOME STRING");

        Mockito.verify(eventApi).create(eventRepresentationCaptor.capture());

        assertEquals("SOME STRING", eventRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SDKException.class)
    public void should_HandleMessage_RethrowSDKException_whenEventApiThrowsSDKException() {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");
        Mockito.when(eventApi.create(Mockito.any(EventSubscriber.EventRepresentation.class))).thenThrow(sdkException);

        eventSubscriber.handleMessage("SOME STRING");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_HandleMessages_NotSupportedByEventSubscriber() {
        eventSubscriber.handleMessages(null);
    }

    @Test
    public void should_onMessage_Successfully() {
        ArgumentCaptor<EventSubscriber.EventRepresentation> eventRepresentationCaptor = ArgumentCaptor.forClass(EventSubscriber.EventRepresentation.class);

        Mockito.when(eventApi.create(Mockito.any(EventSubscriber.EventRepresentation.class))).thenReturn(null);

        try {
            eventSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        Mockito.verify(eventApi).create(eventRepresentationCaptor.capture());

        assertEquals("SOME STRING", eventRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenEventApiThrowsSDKException() throws SubscriberException {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");
        Mockito.when(eventApi.create(Mockito.any(EventSubscriber.EventRepresentation.class))).thenThrow(sdkException);

        try {
            eventSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException ppe) {
            Mockito.verify(platformProvider).markPlatfromAsUnavailable();
            throw ppe;
        }
    }

    @Test
    public void should_onMessage_whenEventApiThrowsSDKException_with_HTTPStatus_400() {
        ArgumentCaptor<EventSubscriber.EventRepresentation> eventRepresentationCaptor = ArgumentCaptor.forClass(EventSubscriber.EventRepresentation.class);

        SDKException sdkException = new SDKException(400, "SOME ERROR MESSAGE");
        Mockito.when(eventApi.create(Mockito.any(EventSubscriber.EventRepresentation.class))).thenThrow(sdkException);

        try {
            eventSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        Mockito.verify(eventApi).create(eventRepresentationCaptor.capture());

        assertEquals("SOME STRING", eventRepresentationCaptor.getValue().toJSON());
    }

    @Test
    public void should_onMessage_whenEventApiThrowsSDKException_with_HTTPStatus_404() {
        ArgumentCaptor<EventSubscriber.EventRepresentation> eventRepresentationCaptor = ArgumentCaptor.forClass(EventSubscriber.EventRepresentation.class);

        SDKException sdkException = new SDKException(404, "SOME ERROR MESSAGE");
        Mockito.when(eventApi.create(Mockito.any(EventSubscriber.EventRepresentation.class))).thenThrow(sdkException);

        try {
            eventSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        Mockito.verify(eventApi).create(eventRepresentationCaptor.capture());

        assertEquals("SOME STRING", eventRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenEventApiThrowsSDKException_with_HTTPStatus_401() throws SubscriberException {
        SDKException sdkException = new SDKException(401, "SOME ERROR MESSAGE");
        Mockito.when(eventApi.create(Mockito.any(EventSubscriber.EventRepresentation.class))).thenThrow(sdkException);

        try {
            eventSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException ppe) {
            Mockito.verify(platformProvider).markPlatfromAsUnavailable();
            throw ppe;
        }
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenEventApiThrowsSDKException_with_HTTPStatus_402() throws SubscriberException {
        SDKException sdkException = new SDKException(402, "SOME ERROR MESSAGE");
        Mockito.when(eventApi.create(Mockito.any(EventSubscriber.EventRepresentation.class))).thenThrow(sdkException);

        try {
            eventSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException ppe) {
            Mockito.verify(platformProvider).markPlatfromAsUnavailable();
            throw ppe;
        }
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenEventApiThrowsSDKException_with_HTTPStatus_408() throws SubscriberException {
        SDKException sdkException = new SDKException(408, "SOME ERROR MESSAGE");
        Mockito.when(eventApi.create(Mockito.any(EventSubscriber.EventRepresentation.class))).thenThrow(sdkException);

        try {
            eventSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException ppe) {
            Mockito.verify(platformProvider).markPlatfromAsUnavailable();
            throw ppe;
        }
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenEventApiThrowsSDKException_with_HTTPStatus_500() throws SubscriberException {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");
        Mockito.when(eventApi.create(Mockito.any(EventSubscriber.EventRepresentation.class))).thenThrow(sdkException);

        try {
            eventSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException ppe) {
            Mockito.verify(platformProvider).markPlatfromAsUnavailable();
            throw ppe;
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_onMessages_NotSupportedByEventSubscriber() throws SubscriberException {
        eventSubscriber.onMessages(null);
    }

    @Test
    public void should_subscribe_successfully() {
        // Transmit rate is initialized with -1
        assertEquals(-1, eventSubscriber.getTransmitRateInSeconds());

        long expectedTransmitRate = 10L;
        Mockito.when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        Mockito.when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationProperties);
        Mockito.when(snmpCommunicationProperties.getTransmitRate()).thenReturn(expectedTransmitRate);

        eventSubscriber.subscribe();

        Mockito.verify(eventPubSub).subscribe(eventSubscriber);
        assertEquals(expectedTransmitRate, eventSubscriber.getTransmitRateInSeconds());
    }

    @Test
    public void should_unsubscribe_successfully() {
        // Transmit rate is initialized with -1
        assertEquals(-1, eventSubscriber.getTransmitRateInSeconds());

        eventSubscriber.unsubscribe();

        Mockito.verify(eventPubSub).unsubscribe(eventSubscriber);
    }

    @Test
    public void should_refreshSubscription_skipAsEventSubscriber_doesNotSupportBatching() {
        // Transmit rate is initialized with -1
        assertEquals(-1, eventSubscriber.getTransmitRateInSeconds());

        eventSubscriber.refreshSubscription();

        Mockito.verifyZeroInteractions(gatewayDataProvider);
        Mockito.verifyZeroInteractions(gatewayManagedObjectWrapper);
        Mockito.verifyZeroInteractions(snmpCommunicationProperties);

        Mockito.verifyZeroInteractions(eventPubSub);

        assertEquals(-1, eventSubscriber.getTransmitRateInSeconds());
    }

    @Test
    public void isReady_should_invoke_isPlatformAvailable() {
        Mockito.when(platformProvider.isPlatformAvailable()).thenReturn(Boolean.TRUE);

        assertTrue(eventSubscriber.isReadyToAcceptMessages());

        Mockito.verify(platformProvider).isPlatformAvailable();
    }
}