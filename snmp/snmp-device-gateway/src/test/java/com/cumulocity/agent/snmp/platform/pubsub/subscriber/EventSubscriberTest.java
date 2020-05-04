/*
 * Copyright © 2012 - 2017 Cumulocity GmbH.
 * Copyright © 2017 - 2020 Software AG, Darmstadt, Germany and/or its licensors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.GatewayManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.pubsub.service.EventPubSub;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.agent.snmp.platform.service.PlatformProvider;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.event.EventApi;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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

    private ArgumentCaptor<EventSubscriber.EventRepresentation> eventRepresentationCaptor;

    @Before
    public void setup() {
    	eventRepresentationCaptor = ArgumentCaptor.forClass(EventSubscriber.EventRepresentation.class);
    }

    @Test
    public void shouldGetBatchingSupportedAsFalse() {
        assertFalse(eventSubscriber.isBatchingSupported());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldFailWithAnExceptionIfBatchingIsTriedForEvents() {
        eventSubscriber.getBatchSize();
    }

    @Test
    public void shouldGetConcurrentSubscriptionsCountAs_10Percent_OfSchedulerPoolSize_1() {

        int gatewayThreadPoolSize = 1;

        when(gatewayProperties.getGatewayThreadPoolSize()).thenReturn(gatewayThreadPoolSize);

        assertEquals(1, eventSubscriber.getConcurrentSubscriptionsCount());
    }

    @Test
    public void shouldGetConcurrentSubscriptionsCountAs_10Percent_OfSchedulerPoolSize_10() {

        int gatewayThreadPoolSize = 10;

        when(gatewayProperties.getGatewayThreadPoolSize()).thenReturn(gatewayThreadPoolSize);

        assertEquals((gatewayThreadPoolSize * 10/100), eventSubscriber.getConcurrentSubscriptionsCount());
    }

    @Test
    public void shouldGetConcurrentSubscriptionsCountAs_10Percent_OfSchedulerPoolSize_101() {

        int schedularPoolSize = 101;

        when(gatewayProperties.getGatewayThreadPoolSize()).thenReturn(schedularPoolSize);

        assertEquals((schedularPoolSize * 10/100), eventSubscriber.getConcurrentSubscriptionsCount());
    }

    @Test
    public void shouldHandleMessageSuccessfully() {
        when(eventApi.create(any(EventSubscriber.EventRepresentation.class))).thenReturn(null);

        eventSubscriber.handleMessage("SOME STRING");

        verify(eventApi).create(eventRepresentationCaptor.capture());

        assertEquals("SOME STRING", eventRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SDKException.class)
    public void should_HandleMessage_RethrowSDKException_whenEventApiThrowsSDKException() {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");
        
        when(eventApi.create(any(EventSubscriber.EventRepresentation.class))).thenThrow(sdkException);

        eventSubscriber.handleMessage("SOME STRING");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_HandleMessages_NotSupportedByEventSubscriber() {
        eventSubscriber.handleMessages(null);
    }

    @Test
    public void should_onMessage_Successfully() {
        when(eventApi.create(any(EventSubscriber.EventRepresentation.class))).thenReturn(null);

        try {
            eventSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        verify(eventApi).create(eventRepresentationCaptor.capture());
        assertEquals("SOME STRING", eventRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenEventApiThrowsSDKException_with_HTTPStatus_400() throws SubscriberException {
        SDKException sdkException = new SDKException(400, "SOME ERROR MESSAGE");

        when(eventApi.create(any(EventSubscriber.EventRepresentation.class))).thenThrow(sdkException);

        try {
            eventSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException e) {
            verifyZeroInteractions(platformProvider);
            verify(eventApi).create(eventRepresentationCaptor.capture());
            assertEquals("SOME STRING", eventRepresentationCaptor.getValue().toJSON());

            throw e;
        }
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenEventApiThrowsSDKException_with_HTTPStatus_404() throws SubscriberException {
        SDKException sdkException = new SDKException(404, "SOME ERROR MESSAGE");

        when(eventApi.create(any(EventSubscriber.EventRepresentation.class))).thenThrow(sdkException);

        try {
            eventSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException e) {
            verifyZeroInteractions(platformProvider);
            verify(eventApi).create(eventRepresentationCaptor.capture());
            assertEquals("SOME STRING", eventRepresentationCaptor.getValue().toJSON());

            throw e;
        }
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenEventApiThrowsSDKException_with_HTTPStatus_500() throws SubscriberException {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");

        when(eventApi.create(any(EventSubscriber.EventRepresentation.class))).thenThrow(sdkException);

        try {
            eventSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException ppe) {
            verify(platformProvider).markPlatfromAsUnavailable();
            verify(eventApi).create(eventRepresentationCaptor.capture());
            assertEquals("SOME STRING", eventRepresentationCaptor.getValue().toJSON());

            throw ppe;
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_onMessages_NotSupportedByEventSubscriber() throws SubscriberException {
        eventSubscriber.onMessages(null);
    }

    @Test
    public void should_subscribe_successfully() {
        long expectedTransmitRate = 10L;

        when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationProperties);
        when(snmpCommunicationProperties.getTransmitRate()).thenReturn(expectedTransmitRate);

        // Transmit rate is initialized with -1
        assertEquals(-1, eventSubscriber.getTransmitRateInSeconds());

        eventSubscriber.subscribe();

        verify(eventPubSub).subscribe(eventSubscriber);
        assertEquals(expectedTransmitRate, eventSubscriber.getTransmitRateInSeconds());
    }

    @Test
    public void should_unsubscribe_successfully() {
        // Transmit rate is initialized with -1
        assertEquals(-1, eventSubscriber.getTransmitRateInSeconds());

        eventSubscriber.unsubscribe();

        verify(eventPubSub).unsubscribe(eventSubscriber);
    }

    @Test
    public void should_refreshSubscription_skipAsEventSubscriber_doesNotSupportBatching() {
        // Transmit rate is initialized with -1
        assertEquals(-1, eventSubscriber.getTransmitRateInSeconds());

        eventSubscriber.refreshSubscription();

        verifyZeroInteractions(gatewayDataProvider);
        verifyZeroInteractions(gatewayManagedObjectWrapper);
        verifyZeroInteractions(snmpCommunicationProperties);
        verifyZeroInteractions(eventPubSub);

        assertEquals(-1, eventSubscriber.getTransmitRateInSeconds());
    }

    @Test
    public void isReady_should_invoke_isPlatformAvailable() {
        when(platformProvider.isPlatformAvailable()).thenReturn(Boolean.TRUE);

        assertTrue(eventSubscriber.isReadyToAcceptMessages());

        verify(platformProvider).isPlatformAvailable();
    }
}