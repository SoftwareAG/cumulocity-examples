package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import com.cumulocity.agent.snmp.config.ConcurrencyConfiguration;
import com.cumulocity.agent.snmp.platform.model.GatewayManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.pubsub.service.MeasurementPubSub;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class MeasurementSubscriberTest {

    public static final List<String> JSON_STRINGS = Arrays.asList("Message 1", "Message 2");

    @Mock
    private ConcurrencyConfiguration concurrencyConfiguration;

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

    @InjectMocks
    private MeasurementSubscriber measurementSubscriber;


    @Test
    public void shouldGetBatchingSupportedAsFalse() {
        assertTrue(measurementSubscriber.isBatchingSupported());
    }

    @Test
    public void shouldGetDefaultBatchSize() {
        assertEquals(200, measurementSubscriber.getBatchSize());
    }

    @Test
    public void shouldGetConcurrentSubscriptionsCountAs_30Percent_OfSchedulerPoolSize_10() {

        int schedularPoolSize = 10;

        Mockito.when(concurrencyConfiguration.getSchedulerPoolSize()).thenReturn(schedularPoolSize);

        assertEquals((schedularPoolSize * 30/100), measurementSubscriber.getConcurrentSubscriptionsCount());
    }

    @Test
    public void shouldGetConcurrentSubscriptionsCountAs_30Percent_OfSchedulerPoolSize_101() {

        int schedularPoolSize = 101;

        Mockito.when(concurrencyConfiguration.getSchedulerPoolSize()).thenReturn(schedularPoolSize);

        assertEquals((schedularPoolSize * 30/100), measurementSubscriber.getConcurrentSubscriptionsCount());
    }

    @Test
    public void shouldHandleMessageSuccessfully() {
        ArgumentCaptor<MeasurementSubscriber.MeasurementRepresentation> measurementRepresentationCaptor = ArgumentCaptor.forClass(MeasurementSubscriber.MeasurementRepresentation.class);

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
        ArgumentCaptor<MeasurementSubscriber.MeasurementCollectionRepresentation> measurementCollectionRepresentation = ArgumentCaptor.forClass(MeasurementSubscriber.MeasurementCollectionRepresentation.class);

        Mockito.when(measurementApi.createBulk(Mockito.any(MeasurementSubscriber.MeasurementCollectionRepresentation.class))).thenReturn(null);

        measurementSubscriber.handleMessages(JSON_STRINGS);

        Mockito.verify(measurementApi).createBulk(measurementCollectionRepresentation.capture());

        assertEquals("{\"measurements\":["
                + String.join(",", JSON_STRINGS)
                + "]}", measurementCollectionRepresentation.getValue().toJSON());
    }

    @Test(expected = SDKException.class)
    public void should_HandleMessages_RethrowSDKException_whenMeasurementApiThrowsSDKException() {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.createBulk(Mockito.any(MeasurementSubscriber.MeasurementCollectionRepresentation.class))).thenThrow(sdkException);

        List<String> jsonStrings = Arrays.asList("Message 1", "Message 2");
        measurementSubscriber.handleMessages(jsonStrings);
    }

    @Test
    public void should_onMessage_Successfully() {
        ArgumentCaptor<MeasurementSubscriber.MeasurementRepresentation> measurementRepresentationCaptor = ArgumentCaptor.forClass(MeasurementSubscriber.MeasurementRepresentation.class);

        Mockito.when(measurementApi.create(Mockito.any(MeasurementSubscriber.MeasurementRepresentation.class))).thenReturn(null);

        measurementSubscriber.onMessage("SOME STRING");

        Mockito.verify(measurementApi).create(measurementRepresentationCaptor.capture());

        assertEquals("SOME STRING", measurementRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SDKException.class)
    public void should_onMessage_whenMeasurementApiThrowsSDKException() {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.create(Mockito.any(MeasurementSubscriber.MeasurementRepresentation.class))).thenThrow(sdkException);

        measurementSubscriber.onMessage("SOME STRING");
    }

    @Test
    public void should_onMessage_whenMeasurementApiThrowsSDKException_with_HTTPStatus_400() {
        ArgumentCaptor<MeasurementSubscriber.MeasurementRepresentation> measurementRepresentationCaptor = ArgumentCaptor.forClass(MeasurementSubscriber.MeasurementRepresentation.class);

        SDKException sdkException = new SDKException(400, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.create(Mockito.any(MeasurementSubscriber.MeasurementRepresentation.class))).thenThrow(sdkException);

        measurementSubscriber.onMessage("SOME STRING");

        Mockito.verify(measurementApi).create(measurementRepresentationCaptor.capture());

        assertEquals("SOME STRING", measurementRepresentationCaptor.getValue().toJSON());
    }

    @Test
    public void should_onMessage_whenMeasurementApiThrowsSDKException_with_HTTPStatus_404() {
        ArgumentCaptor<MeasurementSubscriber.MeasurementRepresentation> measurementRepresentationCaptor = ArgumentCaptor.forClass(MeasurementSubscriber.MeasurementRepresentation.class);

        SDKException sdkException = new SDKException(404, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.create(Mockito.any(MeasurementSubscriber.MeasurementRepresentation.class))).thenThrow(sdkException);

        measurementSubscriber.onMessage("SOME STRING");

        Mockito.verify(measurementApi).create(measurementRepresentationCaptor.capture());

        assertEquals("SOME STRING", measurementRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SDKException.class)
    public void should_onMessage_whenMeasurementApiThrowsSDKException_with_HTTPStatus_401() {
        SDKException sdkException = new SDKException(401, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.create(Mockito.any(MeasurementSubscriber.MeasurementRepresentation.class))).thenThrow(sdkException);

        measurementSubscriber.onMessage("SOME STRING");
    }

    @Test(expected = SDKException.class)
    public void should_onMessage_whenMeasurementApiThrowsSDKException_with_HTTPStatus_402() {
        SDKException sdkException = new SDKException(402, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.create(Mockito.any(MeasurementSubscriber.MeasurementRepresentation.class))).thenThrow(sdkException);

        measurementSubscriber.onMessage("SOME STRING");
    }

    @Test(expected = SDKException.class)
    public void should_onMessage_whenMeasurementApiThrowsSDKException_with_HTTPStatus_408() {
        SDKException sdkException = new SDKException(408, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.create(Mockito.any(MeasurementSubscriber.MeasurementRepresentation.class))).thenThrow(sdkException);

        measurementSubscriber.onMessage("SOME STRING");
    }

    @Test(expected = SDKException.class)
    public void should_onMessage_whenMeasurementApiThrowsSDKException_with_HTTPStatus_500() {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.create(Mockito.any(MeasurementSubscriber.MeasurementRepresentation.class))).thenThrow(sdkException);

        measurementSubscriber.onMessage("SOME STRING");
    }

    @Test
    public void should_onMessages_Successfully() {
        ArgumentCaptor<MeasurementSubscriber.MeasurementCollectionRepresentation> measurementCollectionRepresentationCaptor = ArgumentCaptor.forClass(MeasurementSubscriber.MeasurementCollectionRepresentation.class);

        Mockito.when(measurementApi.createBulk(Mockito.any(MeasurementSubscriber.MeasurementCollectionRepresentation.class))).thenReturn(null);

        List<String> jsonStrings = Arrays.asList("Message 1", "Message 2");
        measurementSubscriber.onMessages(jsonStrings);

        Mockito.verify(measurementApi).createBulk(measurementCollectionRepresentationCaptor.capture());

        assertEquals("{\"measurements\":["
                + String.join(",", jsonStrings)
                + "]}", measurementCollectionRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SDKException.class)
    public void should_onMessages_whenMeasurementApiThrowsSDKException() {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.createBulk(Mockito.any(MeasurementSubscriber.MeasurementCollectionRepresentation.class))).thenThrow(sdkException);

        List<String> jsonStrings = Arrays.asList("Message 1", "Message 2");
        measurementSubscriber.onMessages(jsonStrings);
    }

    @Test
    public void should_onMessages_whenMeasurementApiThrowsSDKException_with_HTTPStatus_400() {
        ArgumentCaptor<MeasurementSubscriber.MeasurementCollectionRepresentation> measurementCollectionRepresentationCaptor = ArgumentCaptor.forClass(MeasurementSubscriber.MeasurementCollectionRepresentation.class);

        SDKException sdkException = new SDKException(400, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.createBulk(Mockito.any(MeasurementSubscriber.MeasurementCollectionRepresentation.class))).thenThrow(sdkException);

        List<String> jsonStrings = Arrays.asList("Message 1", "Message 2");
        measurementSubscriber.onMessages(jsonStrings);

        Mockito.verify(measurementApi).createBulk(measurementCollectionRepresentationCaptor.capture());

        assertEquals("{\"measurements\":["
                + String.join(",", jsonStrings)
                + "]}", measurementCollectionRepresentationCaptor.getValue().toJSON());
    }

    @Test
    public void should_onMessages_whenMeasurementApiThrowsSDKException_with_HTTPStatus_404() {
        ArgumentCaptor<MeasurementSubscriber.MeasurementCollectionRepresentation> measurementCollectionRepresentationCaptor = ArgumentCaptor.forClass(MeasurementSubscriber.MeasurementCollectionRepresentation.class);

        SDKException sdkException = new SDKException(404, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.createBulk(Mockito.any(MeasurementSubscriber.MeasurementCollectionRepresentation.class))).thenThrow(sdkException);

        List<String> jsonStrings = Arrays.asList("Message 1", "Message 2");
        measurementSubscriber.onMessages(jsonStrings);

        Mockito.verify(measurementApi).createBulk(measurementCollectionRepresentationCaptor.capture());

        assertEquals("{\"measurements\":["
                + String.join(",", jsonStrings)
                + "]}", measurementCollectionRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SDKException.class)
    public void should_onMessages_whenMeasurementApiThrowsSDKException_with_HTTPStatus_401() {
        SDKException sdkException = new SDKException(401, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.createBulk(Mockito.any(MeasurementSubscriber.MeasurementCollectionRepresentation.class))).thenThrow(sdkException);

        List<String> jsonStrings = Arrays.asList("Message 1", "Message 2");
        measurementSubscriber.onMessages(jsonStrings);
    }

    @Test(expected = SDKException.class)
    public void should_onMessages_whenMeasurementApiThrowsSDKException_with_HTTPStatus_402() {
        SDKException sdkException = new SDKException(402, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.createBulk(Mockito.any(MeasurementSubscriber.MeasurementCollectionRepresentation.class))).thenThrow(sdkException);

        List<String> jsonStrings = Arrays.asList("Message 1", "Message 2");
        measurementSubscriber.onMessages(jsonStrings);
    }

    @Test(expected = SDKException.class)
    public void should_onMessages_whenMeasurementApiThrowsSDKException_with_HTTPStatus_408() {
        SDKException sdkException = new SDKException(408, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.createBulk(Mockito.any(MeasurementSubscriber.MeasurementCollectionRepresentation.class))).thenThrow(sdkException);

        List<String> jsonStrings = Arrays.asList("Message 1", "Message 2");
        measurementSubscriber.onMessages(jsonStrings);
    }

    @Test(expected = SDKException.class)
    public void should_onMessages_whenMeasurementApiThrowsSDKException_with_HTTPStatus_500() {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");
        Mockito.when(measurementApi.createBulk(Mockito.any(MeasurementSubscriber.MeasurementCollectionRepresentation.class))).thenThrow(sdkException);

        List<String> jsonStrings = Arrays.asList("Message 1", "Message 2");
        measurementSubscriber.onMessages(jsonStrings);
    }

    @Test
    public void should_subscribe_successfully() {
        // Transmit rate is initialized with -1
        assertEquals(-1, measurementSubscriber.getTransmitRateInSeconds());

        long expectedTransmitRate = 10L;
        Mockito.when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        Mockito.when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationProperties);
        Mockito.when(snmpCommunicationProperties.getTransmitRate()).thenReturn(expectedTransmitRate);

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
    public void should_refreshSubscription_successfully() {
        // Transmit rate is initialized with -1
        assertEquals(-1, measurementSubscriber.getTransmitRateInSeconds());

        long expectedTransmitRate = 10L;
        Mockito.when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        Mockito.when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationProperties);
        Mockito.when(snmpCommunicationProperties.getTransmitRate()).thenReturn(expectedTransmitRate);

        measurementSubscriber.refreshSubscription();

        Mockito.verify(measurementPubSub).unsubscribe(measurementSubscriber);
        Mockito.verify(measurementPubSub).subscribe(measurementSubscriber);

        assertEquals(expectedTransmitRate, measurementSubscriber.getTransmitRateInSeconds());
    }

    @Test
    public void should_skipRefreshSubscription_when_transmitRateDoesNotChange() {
        // Transmit rate is initialized with -1
        assertEquals(-1, measurementSubscriber.getTransmitRateInSeconds());

        long expectedTransmitRate = -1;
        Mockito.when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        Mockito.when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationProperties);
        Mockito.when(snmpCommunicationProperties.getTransmitRate()).thenReturn(expectedTransmitRate);

        measurementSubscriber.refreshSubscription();

        Mockito.verifyZeroInteractions(measurementPubSub);

        assertEquals(-1, measurementSubscriber.getTransmitRateInSeconds());
    }
}