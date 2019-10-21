package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.GatewayManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.pubsub.service.AlarmPubSub;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.agent.snmp.platform.service.PlatformProvider;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AlarmSubscriberTest {

    @Mock
    private GatewayProperties.SnmpProperties snmpProperties;

    @Mock
    private GatewayDataProvider gatewayDataProvider;

    @Mock
    private AlarmPubSub alarmPubSub;

    @Mock
    private AlarmApi alarmApi;

    @Mock
    private GatewayManagedObjectWrapper gatewayManagedObjectWrapper;

    @Mock
    private GatewayManagedObjectWrapper.SnmpCommunicationProperties snmpCommunicationProperties;

    @Mock
    private PlatformProvider platformProvider;

    @InjectMocks
    private AlarmSubscriber alarmSubscriber;


    @Test
    public void shouldGetBatchingSupportedAsFalse() {
        assertFalse(alarmSubscriber.isBatchingSupported());
    }

    @Test
    public void shouldGetDefaultBatchSize() {
        assertEquals(200, alarmSubscriber.getBatchSize());
    }

    @Test
    public void shouldGetConcurrentSubscriptionsCountAs_10Percent_OfSchedulerPoolSize_1() {

        int schedularPoolSize = 1;

        Mockito.when(snmpProperties.getTrapListenerThreadPoolSize()).thenReturn(schedularPoolSize);

        assertEquals(1, alarmSubscriber.getConcurrentSubscriptionsCount());
    }

    @Test
    public void shouldGetConcurrentSubscriptionsCountAs_10Percent_OfSchedulerPoolSize_10() {

        int schedularPoolSize = 10;

        Mockito.when(snmpProperties.getTrapListenerThreadPoolSize()).thenReturn(schedularPoolSize);

        assertEquals((schedularPoolSize * 10/100), alarmSubscriber.getConcurrentSubscriptionsCount());
    }

    @Test
    public void shouldGetConcurrentSubscriptionsCountAs_10Percent_OfSchedulerPoolSize_101() {

        int schedularPoolSize = 101;

        Mockito.when(snmpProperties.getTrapListenerThreadPoolSize()).thenReturn(schedularPoolSize);

        assertEquals((schedularPoolSize * 10/100), alarmSubscriber.getConcurrentSubscriptionsCount());
    }

    @Test
    public void shouldHandleMessageSuccessfully() {
        ArgumentCaptor<AlarmSubscriber.AlarmRepresentation> alarmRepresentationCaptor = ArgumentCaptor.forClass(AlarmSubscriber.AlarmRepresentation.class);

        Mockito.when(alarmApi.create(Mockito.any(AlarmSubscriber.AlarmRepresentation.class))).thenReturn(null);

        alarmSubscriber.handleMessage("SOME STRING");

        Mockito.verify(alarmApi).create(alarmRepresentationCaptor.capture());

        assertEquals("SOME STRING", alarmRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SDKException.class)
    public void should_HandleMessage_RethrowSDKException_whenAlarmApiThrowsSDKException() {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");
        Mockito.when(alarmApi.create(Mockito.any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

        alarmSubscriber.handleMessage("SOME STRING");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_HandleMessages_NotSupportedByAlarmSubscriber() {
        alarmSubscriber.handleMessages(null);
    }

    @Test
    public void should_onMessage_Successfully() {
        ArgumentCaptor<AlarmSubscriber.AlarmRepresentation> alarmRepresentationCaptor = ArgumentCaptor.forClass(AlarmSubscriber.AlarmRepresentation.class);

        Mockito.when(alarmApi.create(Mockito.any(AlarmSubscriber.AlarmRepresentation.class))).thenReturn(null);

        try {
            alarmSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        Mockito.verify(alarmApi).create(alarmRepresentationCaptor.capture());

        assertEquals("SOME STRING", alarmRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenAlarmApiThrowsSDKException() throws SubscriberException {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");
        Mockito.when(alarmApi.create(Mockito.any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

        try {
            alarmSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException ppe) {
            Mockito.verify(platformProvider).markPlatfromAsUnavailable();
            throw ppe;
        }
    }

    @Test
    public void should_onMessage_whenAlarmApiThrowsSDKException_with_HTTPStatus_400() {
        ArgumentCaptor<AlarmSubscriber.AlarmRepresentation> alarmRepresentationCaptor = ArgumentCaptor.forClass(AlarmSubscriber.AlarmRepresentation.class);

        SDKException sdkException = new SDKException(400, "SOME ERROR MESSAGE");
        Mockito.when(alarmApi.create(Mockito.any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

        try {
            alarmSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        Mockito.verify(alarmApi).create(alarmRepresentationCaptor.capture());

        assertEquals("SOME STRING", alarmRepresentationCaptor.getValue().toJSON());
    }

    @Test
    public void should_onMessage_whenAlarmApiThrowsSDKException_with_HTTPStatus_404() {
        ArgumentCaptor<AlarmSubscriber.AlarmRepresentation> alarmRepresentationCaptor = ArgumentCaptor.forClass(AlarmSubscriber.AlarmRepresentation.class);

        SDKException sdkException = new SDKException(404, "SOME ERROR MESSAGE");
        Mockito.when(alarmApi.create(Mockito.any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

        try {
            alarmSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        Mockito.verify(alarmApi).create(alarmRepresentationCaptor.capture());

        assertEquals("SOME STRING", alarmRepresentationCaptor.getValue().toJSON());
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenAlarmApiThrowsSDKException_with_HTTPStatus_401() throws SubscriberException {
        SDKException sdkException = new SDKException(401, "SOME ERROR MESSAGE");
        Mockito.when(alarmApi.create(Mockito.any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

        try {
            alarmSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException ppe) {
            Mockito.verify(platformProvider).markPlatfromAsUnavailable();
            throw ppe;
        }
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenAlarmApiThrowsSDKException_with_HTTPStatus_402() throws SubscriberException {
        SDKException sdkException = new SDKException(402, "SOME ERROR MESSAGE");
        Mockito.when(alarmApi.create(Mockito.any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

        try {
            alarmSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException ppe) {
            Mockito.verify(platformProvider).markPlatfromAsUnavailable();
            throw ppe;
        }
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenAlarmApiThrowsSDKException_with_HTTPStatus_408() throws SubscriberException {
        SDKException sdkException = new SDKException(408, "SOME ERROR MESSAGE");
        Mockito.when(alarmApi.create(Mockito.any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

        try {
            alarmSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException ppe) {
            Mockito.verify(platformProvider).markPlatfromAsUnavailable();
            throw ppe;
        }
    }

    @Test(expected = SubscriberException.class)
    public void should_onMessage_whenAlarmApiThrowsSDKException_with_HTTPStatus_500() throws SubscriberException {
        SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");
        Mockito.when(alarmApi.create(Mockito.any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

        try {
            alarmSubscriber.onMessage("SOME STRING");
        } catch (SubscriberException ppe) {
            Mockito.verify(platformProvider).markPlatfromAsUnavailable();
            throw ppe;
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_onMessages_NotSupportedByAlarmSubscriber() throws SubscriberException {
        alarmSubscriber.onMessages(null);
    }

    @Test
    public void should_subscribe_successfully() {
        // Transmit rate is initialized with -1
        assertEquals(-1, alarmSubscriber.getTransmitRateInSeconds());

        long expectedTransmitRate = 10L;
        Mockito.when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        Mockito.when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationProperties);
        Mockito.when(snmpCommunicationProperties.getTransmitRate()).thenReturn(expectedTransmitRate);

        alarmSubscriber.subscribe();

        Mockito.verify(alarmPubSub).subscribe(alarmSubscriber);
        assertEquals(expectedTransmitRate, alarmSubscriber.getTransmitRateInSeconds());
    }

    @Test
    public void should_unsubscribe_successfully() {
        // Transmit rate is initialized with -1
        assertEquals(-1, alarmSubscriber.getTransmitRateInSeconds());

        alarmSubscriber.unsubscribe();

        Mockito.verify(alarmPubSub).unsubscribe(alarmSubscriber);
    }

    @Test
    public void should_refreshSubscription_skipAsAlarmSubscriber_doesNotSupportBatching() {
        // Transmit rate is initialized with -1
        assertEquals(-1, alarmSubscriber.getTransmitRateInSeconds());

        alarmSubscriber.refreshSubscription();

        Mockito.verifyZeroInteractions(gatewayDataProvider);
        Mockito.verifyZeroInteractions(gatewayManagedObjectWrapper);
        Mockito.verifyZeroInteractions(snmpCommunicationProperties);

        Mockito.verifyZeroInteractions(alarmPubSub);

        assertEquals(-1, alarmSubscriber.getTransmitRateInSeconds());
    }

    @Test
    public void isReady_should_invoke_isPlatformAvailable() {
        Mockito.when(platformProvider.isPlatformAvailable()).thenReturn(Boolean.TRUE);

        assertTrue(alarmSubscriber.isReadyToAcceptMessages());

        Mockito.verify(platformProvider).isPlatformAvailable();
    }
}