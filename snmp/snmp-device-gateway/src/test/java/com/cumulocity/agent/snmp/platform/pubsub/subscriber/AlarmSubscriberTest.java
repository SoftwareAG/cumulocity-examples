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
import com.cumulocity.agent.snmp.platform.pubsub.service.AlarmPubSub;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.agent.snmp.platform.service.PlatformProvider;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

	@Test(expected = UnsupportedOperationException.class)
	public void shouldFailWithAnExceptionIfBatchingIsTriedForAlarms() {
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
	public void should_onMessage_whenAlarmApiThrowsSDKException_with_HTTPStatus_400() throws SubscriberException {
		SDKException sdkException = new SDKException(400, "SOME ERROR MESSAGE");

		when(alarmApi.create(any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

		try {
			alarmSubscriber.onMessage("SOME STRING");
		} catch (SubscriberException e) {
			verifyZeroInteractions(platformProvider);
			verify(alarmApi).create(alarmRepresentationCaptor.capture());
			assertEquals("SOME STRING", alarmRepresentationCaptor.getValue().toJSON());

			throw e;
		}
	}

	@Test(expected = SubscriberException.class)
	public void should_onMessage_whenAlarmApiThrowsSDKException_with_HTTPStatus_404() throws SubscriberException {
		SDKException sdkException = new SDKException(404, "SOME ERROR MESSAGE");

		when(alarmApi.create(any(AlarmSubscriber.AlarmRepresentation.class))).thenThrow(sdkException);

		try {
			alarmSubscriber.onMessage("SOME STRING");
		} catch (SubscriberException e) {
			verifyZeroInteractions(platformProvider);
			verify(alarmApi).create(alarmRepresentationCaptor.capture());
			assertEquals("SOME STRING", alarmRepresentationCaptor.getValue().toJSON());

			throw e;
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
			verify(alarmApi).create(alarmRepresentationCaptor.capture());
			assertEquals("SOME STRING", alarmRepresentationCaptor.getValue().toJSON());

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