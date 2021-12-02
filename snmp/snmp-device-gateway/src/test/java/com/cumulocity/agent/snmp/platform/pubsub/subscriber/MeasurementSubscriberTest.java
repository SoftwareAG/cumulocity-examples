/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
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
import com.cumulocity.agent.snmp.platform.pubsub.service.MeasurementPubSub;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.agent.snmp.platform.service.PlatformProvider;
import com.cumulocity.rest.representation.measurement.MeasurementCollectionRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MeasurementSubscriberTest {

    private static final List<String> JSON_STRINGS = Arrays.asList("Message 1", "Message 2");

    @Mock
    private GatewayProperties gatewayProperties;

    @Mock
    private GatewayDataProvider gatewayDataProvider;

    @Mock
    private MeasurementPubSub measurementPubSub;

    @Mock
    private MeasurementApi measurementApi;

    @Mock
    private GatewayManagedObjectWrapper gatewayManagedObjectWrapper;

    @Mock
    private GatewayManagedObjectWrapper.SnmpCommunicationProperties snmpCommunicationProperties;

    @Mock
    private PlatformProvider platformProvider;

    @InjectMocks
    private MeasurementSubscriber measurementSubscriber;

    private ArgumentCaptor<MeasurementSubscriber.MeasurementRepresentation> measurementRepresentationCaptor;

    private ArgumentCaptor<MeasurementSubscriber.MeasurementCollectionRepresentation> measurementCollectionRepresentationCaptor;

    @Before
    public void setup() {
    	measurementRepresentationCaptor = ArgumentCaptor.forClass(MeasurementSubscriber.MeasurementRepresentation.class);
    	measurementCollectionRepresentationCaptor = ArgumentCaptor.forClass(MeasurementSubscriber.MeasurementCollectionRepresentation.class);
    }

    @Test
    public void shouldGetBatchingSupportedAsFalse() {
        assertTrue(measurementSubscriber.isBatchingSupported());
    }

    @Test
    public void shouldGetDefaultBatchSize() {
    	when(gatewayProperties.getGatewayMaxBatchSize()).thenReturn(500);

        assertEquals(500, measurementSubscriber.getBatchSize());
    }

    @Test
    public void shouldGetConcurrentSubscriptionsCountAs_30Percent_OfSchedulerPoolSize_1() {

        int schedularPoolSize = 1;

        Mockito.when(gatewayProperties.getGatewayThreadPoolSize()).thenReturn(schedularPoolSize);

        assertEquals(3, measurementSubscriber.getConcurrentSubscriptionsCount());
    }

    @Test
    public void shouldGetConcurrentSubscriptionsCountAs_30Percent_OfSchedulerPoolSize_10() {

        int schedularPoolSize = 10;

        Mockito.when(gatewayProperties.getGatewayThreadPoolSize()).thenReturn(schedularPoolSize);

        assertEquals((schedularPoolSize * 30/100), measurementSubscriber.getConcurrentSubscriptionsCount());
    }

    @Test
    public void shouldGetConcurrentSubscriptionsCountAs_30Percent_OfSchedulerPoolSize_101() {

        int schedularPoolSize = 101;

        Mockito.when(gatewayProperties.getGatewayThreadPoolSize()).thenReturn(schedularPoolSize);

        assertEquals((schedularPoolSize * 30/100), measurementSubscriber.getConcurrentSubscriptionsCount());
    }

    @Test
    public void shouldHandleMessageSuccessfully() {
        Mockito.when(measurementApi.create(Mockito.any(MeasurementSubscriber.MeasurementRepresentation.class))).thenReturn(null);

        measurementSubscriber.handleMessage("SOME STRING");

        Mockito.verify(measurementApi).create(measurementRepresentationCaptor.capture());

        assertEquals("SOME STRING", measurementRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SDKException.class)
    public void should_HandleMessage_RethrowSDKException_whenMeasurementApiThrowsSDKException() {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.create(Mockito.any(MeasurementSubscriber.MeasurementRepresentation.class))).thenThrow(sdkException);

        measurementSubscriber.handleMessage("SOME STRING");
    }

    @Test
    public void shouldHandleMessagesSuccessfully() {

        measurementSubscriber.handleMessages(JSON_STRINGS);

        Mockito.verify(measurementApi).createBulkWithoutResponse(measurementCollectionRepresentationCaptor.capture());

        assertEquals("{\"measurements\":["
                + String.join(",", JSON_STRINGS)
                + "]}", measurementCollectionRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SDKException.class)
    public void should_HandleMessages_RethrowSDKException_whenMeasurementApiThrowsSDKException() {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");
        doThrow(sdkException).when(measurementApi).createBulkWithoutResponse(any(MeasurementCollectionRepresentation.class));
        List<String> jsonStrings = Arrays.asList("Message 1", "Message 2");
        measurementSubscriber.handleMessages(jsonStrings);
    }

    @Test
    public void should_onMessage_Successfully() {
        Mockito.when(measurementApi.create(Mockito.any(MeasurementSubscriber.MeasurementRepresentation.class))).thenReturn(null);

        try {
            measurementSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        Mockito.verify(measurementApi).create(measurementRepresentationCaptor.capture());

        assertEquals("SOME STRING", measurementRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenMeasurementApiThrowsSDKException_with_HTTPStatus_400() throws SubscriberException {
        SDKException sdkException = new SDKException(400, "SOME ERROR MESSAGE");

        when(measurementApi.create(any(MeasurementSubscriber.MeasurementRepresentation.class))).thenThrow(sdkException);

        try {
            measurementSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException e) {
            verifyNoInteractions(platformProvider);
            verify(measurementApi).create(measurementRepresentationCaptor.capture());
            assertEquals("SOME STRING", measurementRepresentationCaptor.getValue().toJSON());

            throw e;
        }
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenMeasurementApiThrowsSDKException_with_HTTPStatus_404() throws SubscriberException {
        SDKException sdkException = new SDKException(404, "SOME ERROR MESSAGE");

        when(measurementApi.create(any(MeasurementSubscriber.MeasurementRepresentation.class))).thenThrow(sdkException);

        try {
            measurementSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException e) {
            verifyNoInteractions(platformProvider);
            verify(measurementApi).create(measurementRepresentationCaptor.capture());
            assertEquals("SOME STRING", measurementRepresentationCaptor.getValue().toJSON());

            throw e;
        }
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenMeasurementApiThrowsSDKException_with_HTTPStatus_500() throws SubscriberException {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");

        when(measurementApi.create(any(MeasurementSubscriber.MeasurementRepresentation.class))).thenThrow(sdkException);

        try {
            measurementSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException ppe) {
            verify(platformProvider).markPlatfromAsUnavailable();
            verify(measurementApi).create(measurementRepresentationCaptor.capture());
            assertEquals("SOME STRING", measurementRepresentationCaptor.getValue().toJSON());

            throw ppe;
        }
    }

    @Test
    public void should_onMessages_Successfully() {

        List<String> jsonStrings = Arrays.asList("Message 1", "Message 2");
        try {
            measurementSubscriber.onMessages(jsonStrings);
        } catch (SubscriberException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        Mockito.verify(measurementApi).createBulkWithoutResponse(measurementCollectionRepresentationCaptor.capture());

        assertEquals("{\"measurements\":["
                + String.join(",", jsonStrings)
                + "]}", measurementCollectionRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessages_whenMeasurementApiThrowsSDKException_with_HTTPStatus_500() throws SubscriberException {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");
        doThrow(sdkException).when(measurementApi).createBulkWithoutResponse(any(MeasurementCollectionRepresentation.class));

        List<String> jsonStrings = Arrays.asList("Message 1", "Message 2");
        try {
            measurementSubscriber.onMessages(jsonStrings);
        } catch (SubscriberException ppe) {
            verify(platformProvider).markPlatfromAsUnavailable();
            verify(measurementApi).createBulkWithoutResponse(measurementCollectionRepresentationCaptor.capture());
            assertEquals("{\"measurements\":["
                    + String.join(",", jsonStrings)
                    + "]}", measurementCollectionRepresentationCaptor.getValue().toJSON());

            throw ppe;
        }
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessages_whenMeasurementApiThrowsSDKException_with_HTTPStatus_400() throws SubscriberException {
        SDKException sdkException = new SDKException(400, "SOME ERROR MESSAGE");
        doThrow(sdkException).when(measurementApi).createBulkWithoutResponse(any(MeasurementCollectionRepresentation.class));

        List<String> jsonStrings = Arrays.asList("Message 1", "Message 2");
        try {
            measurementSubscriber.onMessages(jsonStrings);
        } catch (SubscriberException e) {
            verifyNoInteractions(platformProvider);
            verify(measurementApi).createBulkWithoutResponse(measurementCollectionRepresentationCaptor.capture());
            assertEquals("{\"measurements\":["
                    + String.join(",", jsonStrings)
                    + "]}", measurementCollectionRepresentationCaptor.getValue().toJSON());

            throw e;
        }
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessages_whenMeasurementApiThrowsSDKException_with_HTTPStatus_404() throws SubscriberException {
        SDKException sdkException = new SDKException(404, "SOME ERROR MESSAGE");
        doThrow(sdkException).when(measurementApi).createBulkWithoutResponse(any(MeasurementCollectionRepresentation.class));

        List<String> jsonStrings = Arrays.asList("Message 1", "Message 2");
        try {
            measurementSubscriber.onMessages(jsonStrings);
        } catch (SubscriberException e) {
            verifyNoInteractions(platformProvider);
            verify(measurementApi).createBulkWithoutResponse(measurementCollectionRepresentationCaptor.capture());
            assertEquals("{\"measurements\":["
                    + String.join(",", jsonStrings)
                    + "]}", measurementCollectionRepresentationCaptor.getValue().toJSON());

            throw e;
        }
    }

    @Test
    public void should_subscribe_successfully() {
        long expectedTransmitRate = 10L;

        Mockito.when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        Mockito.when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationProperties);
        Mockito.when(snmpCommunicationProperties.getTransmitRate()).thenReturn(expectedTransmitRate);

        // Transmit rate is initialized with -1
        assertEquals(-1, measurementSubscriber.getTransmitRateInSeconds());

        measurementSubscriber.subscribe();

        Mockito.verify(measurementPubSub).subscribe(measurementSubscriber);
        assertEquals(expectedTransmitRate, measurementSubscriber.getTransmitRateInSeconds());
    }

    @Test
    public void should_unsubscribe_successfully() {
        // Transmit rate is initialized with -1
        assertEquals(-1, measurementSubscriber.getTransmitRateInSeconds());

        measurementSubscriber.unsubscribe();

        Mockito.verify(measurementPubSub).unsubscribe(measurementSubscriber);
    }

    @Test
    public void should_RefreshSubscription_successfully() {
        long expectedTransmitRate = 10L;

        Mockito.when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        Mockito.when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationProperties);
        Mockito.when(snmpCommunicationProperties.getTransmitRate()).thenReturn(expectedTransmitRate);

        // Transmit rate is initialized with -1
        assertEquals(-1, measurementSubscriber.getTransmitRateInSeconds());

        measurementSubscriber.refreshSubscription();

        Mockito.verify(measurementPubSub).unsubscribe(measurementSubscriber);
        Mockito.verify(measurementPubSub).subscribe(measurementSubscriber);

        assertEquals(expectedTransmitRate, measurementSubscriber.getTransmitRateInSeconds());
    }

    @Test
    public void should_skipRefreshSubscription_when_transmitRateDoesNotChange() {
        long expectedTransmitRate = -1;

        Mockito.when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        Mockito.when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationProperties);
        Mockito.when(snmpCommunicationProperties.getTransmitRate()).thenReturn(expectedTransmitRate);

        // Transmit rate is initialized with -1
        assertEquals(-1, measurementSubscriber.getTransmitRateInSeconds());

        measurementSubscriber.refreshSubscription();

        Mockito.verifyNoInteractions(measurementPubSub);

        assertEquals(-1, measurementSubscriber.getTransmitRateInSeconds());
    }

    @Test
    public void isReady_should_invoke_isPlatformAvailable() {
        Mockito.when(platformProvider.isPlatformAvailable()).thenReturn(Boolean.TRUE);

        assertTrue(measurementSubscriber.isReadyToAcceptMessages());

        Mockito.verify(platformProvider).isPlatformAvailable();
    }
}