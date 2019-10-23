package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.exception.BatchNotSupportedException;
import com.cumulocity.agent.snmp.platform.model.GatewayManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.pubsub.service.AlarmPubSub;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.agent.snmp.platform.service.PlatformProvider;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;

@RunWith(MockitoJUnitRunner.class)
public class AlarmSubscriberTest {

	@Mock
	private GatewayProperties gatewayProperties;

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

	private ArgumentCaptor<AlarmSubscriber.AlarmRepresentation> alarmRepresentationCaptor;

	@Before
	public void setup() {
		alarmRepresentationCaptor = ArgumentCaptor.forClass(AlarmSubscriber.AlarmRepresentation.class);
	}

	@Test
	public void shouldDisableBatchingForAlarms() {
		assertFalse(alarmSubscriber.isBatchingSupported());
	}

	@Test(expected = BatchNotSupportedException.class)
	public void shouldFailWithAnExceptionIfBatchingIsTriedForAlarms() throws BatchNotSupportedException {
		assertEquals(200, alarmSubscriber.getBatchSize());
	}

	@Test
	public void shouldGetConcurrentSubscriptionsCountAs_10Percent_OfTotalThreadPoolSize_1() {

		int gatewayThreadPoolSize = 1;

		when(gatewayProperties.getGatewayThreadPoolSize()).thenReturn(gatewayThreadPoolSize);

		assertEquals(1, alarmSubscriber.getConcurrentSubscriptionsCount());
	}

	@Test
	public void shouldGetConcurrentSubscriptionsCountAs_10Percent_OfTotalThreadPoolSize_10() {

		int gatewayThreadPoolSize = 10;

		when(gatewayProperties.getGatewayThreadPoolSize()).thenReturn(gatewayThreadPoolSize);

		assertEquals((gatewayThreadPoolSize * 10 / 100), alarmSubscriber.getConcurrentSubscriptionsCount());
	}

	@Test
	public void shouldGetConcurrentSubscriptionsCountAs_10Percent_OfTotalThreadPoolSize_101() {

		int gatewayThreadPoolSize = 101;

		when(gatewayProperties.getGatewayThreadPoolSize()).thenReturn(gatewayThreadPoolSize);

		assertEquals((gatewayThreadPoolSize * 10 / 100), alarmSubscriber.getConcurrentSubscriptionsCount());
	}

	@Test
	public void shouldHandleMessageSuccessfully() {
		when(alarmApi.create(any(AlarmSubscriber.AlarmRepresentation.class))).thenReturn(null);

		alarmSubscriber.handleMessage("SOME STRING");

		verify(alarmApi).create(alarmRepresentationCaptor.capture());

		assertEquals("SOME STRING", alarmRepresentationCaptor.getValue().toJSON());
	}

	@Test(expected = SDKException.class)
	public void should_HandleMessage_RethrowSDKException_whenAlarmApiThrowsSDKException() {
		SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");

		when(alarmApi.create(any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

		alarmSubscriber.handleMessage("SOME STRING");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void should_HandleMessages_NotSupportedByAlarmSubscriber() {
		alarmSubscriber.handleMessages(null);
	}

	@Test
	public void should_onMessage_Successfully() {
		when(alarmApi.create(any(AlarmSubscriber.AlarmRepresentation.class))).thenReturn(null);

		try {
			alarmSubscriber.onMessage("SOME STRING");
		} catch (SubscriberException e) {
			fail(e.getMessage());
		}

		verify(alarmApi).create(alarmRepresentationCaptor.capture());

		assertEquals("SOME STRING", alarmRepresentationCaptor.getValue().toJSON());
	}

	@Test(expected = SubscriberException.class)
	public void should_onMessage_whenAlarmApiThrowsSDKException() throws SubscriberException {
		SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");

		when(alarmApi.create(any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

		try {
			alarmSubscriber.onMessage("SOME STRING");
		} catch (SubscriberException ppe) {
			verify(platformProvider).markPlatfromAsUnavailable();
			throw ppe;
		}
	}

	@Test
	public void should_onMessage_whenAlarmApiThrowsSDKException_with_HTTPStatus_400() {
		SDKException sdkException = new SDKException(400, "SOME ERROR MESSAGE");

		when(alarmApi.create(any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

		try {
			alarmSubscriber.onMessage("SOME STRING");
		} catch (SubscriberException e) {
			fail(e.getMessage());
		}

		verify(alarmApi).create(alarmRepresentationCaptor.capture());

		assertEquals("SOME STRING", alarmRepresentationCaptor.getValue().toJSON());
	}

	@Test
	public void should_onMessage_whenAlarmApiThrowsSDKException_with_HTTPStatus_404() {
		SDKException sdkException = new SDKException(404, "SOME ERROR MESSAGE");

		when(alarmApi.create(any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

		try {
			alarmSubscriber.onMessage("SOME STRING");
		} catch (SubscriberException e) {
			fail(e.getMessage());
		}

		verify(alarmApi).create(alarmRepresentationCaptor.capture());

		assertEquals("SOME STRING", alarmRepresentationCaptor.getValue().toJSON());
	}

	@Test(expected = SubscriberException.class)
	public void should_onMessage_whenAlarmApiThrowsSDKException_with_HTTPStatus_401() throws SubscriberException {
		SDKException sdkException = new SDKException(401, "SOME ERROR MESSAGE");

		when(alarmApi.create(any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

		try {
			alarmSubscriber.onMessage("SOME STRING");
		} catch (SubscriberException ppe) {
			verify(platformProvider).markPlatfromAsUnavailable();
			throw ppe;
		}
	}

	@Test(expected = SubscriberException.class)
	public void should_onMessage_whenAlarmApiThrowsSDKException_with_HTTPStatus_402() throws SubscriberException {
		SDKException sdkException = new SDKException(402, "SOME ERROR MESSAGE");

		when(alarmApi.create(any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

		try {
			alarmSubscriber.onMessage("SOME STRING");
		} catch (SubscriberException ppe) {
			verify(platformProvider).markPlatfromAsUnavailable();
			throw ppe;
		}
	}

	@Test(expected = SubscriberException.class)
	public void should_onMessage_whenAlarmApiThrowsSDKException_with_HTTPStatus_408() throws SubscriberException {
		SDKException sdkException = new SDKException(408, "SOME ERROR MESSAGE");

		when(alarmApi.create(any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

		try {
			alarmSubscriber.onMessage("SOME STRING");
		} catch (SubscriberException ppe) {
			verify(platformProvider).markPlatfromAsUnavailable();
			throw ppe;
		}
	}

	@Test(expected = SubscriberException.class)
	public void should_onMessage_whenAlarmApiThrowsSDKException_with_HTTPStatus_500() throws SubscriberException {
		SDKException sdkException = new SDKException(500, "SOME ERROR MESSAGE");

		when(alarmApi.create(any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

		try {
			alarmSubscriber.onMessage("SOME STRING");
		} catch (SubscriberException ppe) {
			verify(platformProvider).markPlatfromAsUnavailable();
			throw ppe;
		}
	}

	@Test(expected = UnsupportedOperationException.class)
	public void should_onMessages_NotSupportedByAlarmSubscriber() throws SubscriberException {
		alarmSubscriber.onMessages(null);
	}

	@Test
	public void should_subscribe_successfully() {
		long expectedTransmitRate = 10L;

		when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
		when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationProperties);
		when(snmpCommunicationProperties.getTransmitRate()).thenReturn(expectedTransmitRate);

		// Transmit rate is initialized with -1
		assertEquals(-1, alarmSubscriber.getTransmitRateInSeconds());

		alarmSubscriber.subscribe();

		verify(alarmPubSub).subscribe(alarmSubscriber);
		assertEquals(expectedTransmitRate, alarmSubscriber.getTransmitRateInSeconds());
	}

	@Test
	public void should_unsubscribe_successfully() {
		// Transmit rate is initialized with -1
		assertEquals(-1, alarmSubscriber.getTransmitRateInSeconds());

		alarmSubscriber.unsubscribe();

		verify(alarmPubSub).unsubscribe(alarmSubscriber);
	}

	@Test
	public void should_refreshSubscription_skipAsAlarmSubscriber_doesNotSupportBatching() {
		// Transmit rate is initialized with -1
		assertEquals(-1, alarmSubscriber.getTransmitRateInSeconds());

		alarmSubscriber.refreshSubscription();

		verifyZeroInteractions(gatewayDataProvider);
		verifyZeroInteractions(gatewayManagedObjectWrapper);
		verifyZeroInteractions(snmpCommunicationProperties);
		verifyZeroInteractions(alarmPubSub);

		assertEquals(-1, alarmSubscriber.getTransmitRateInSeconds());
	}

	@Test
	public void should_invoke_isPlatformAvailable_AsPartOfReadyToAccept() {
		when(platformProvider.isPlatformAvailable()).thenReturn(Boolean.TRUE);

		assertTrue(alarmSubscriber.isReadyToAcceptMessages());

		verify(platformProvider).isPlatformAvailable();
	}
}