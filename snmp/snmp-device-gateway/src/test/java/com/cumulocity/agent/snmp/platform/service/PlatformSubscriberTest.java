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

package com.cumulocity.agent.snmp.platform.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.GatewayManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.ReceivedOperationForGatewayEvent;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.cep.notification.InventoryRealtimeDeleteAwareNotificationsSubscriber;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.devicecontrol.notification.OperationNotificationSubscriber;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;

@RunWith(MockitoJUnitRunner.class)
public class PlatformSubscriberTest {

	@Mock
	private InventoryApi inventoryApi;

	@Mock
	private DeviceControlApi deviceControlApi;

	@Mock
	private GatewayProperties properties;

	@Mock
	private PlatformProvider platformProvider;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	GatewayDataProvider gatewayDataProvider;

	@Spy
	@InjectMocks
	PlatformSubscriber platformSubscriber;

	@Spy
	private ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

	@Captor
	private ArgumentCaptor<SubscriptionListener<GId, OperationRepresentation>> subscriptionListenerCaptor;

	@Captor
	private ArgumentCaptor<ReceivedOperationForGatewayEvent> receivedOperationForGatewayEventCaptor;

	@Before
	public void setup() {
		taskScheduler.initialize();
	}

	@Test
	public void shouldPublishReceivedOperationForGatewayEventWhenDeviceIDsMatch() {
		final GId gatewayDeviceId = GId.asGId("111");
		String gatewayDeviceName = "snmp-agent-test";

		OperationNotificationSubscriber operationNotificationSubscriberMock = mock(
				OperationNotificationSubscriber.class);
		when(deviceControlApi.getNotificationsSubscriber()).thenReturn(operationNotificationSubscriberMock);

		// Inject the gatewayDevice mock
		GatewayManagedObjectWrapper gatewayDeviceMock = mock(GatewayManagedObjectWrapper.class);
		when(gatewayDeviceMock.getId()).thenReturn(gatewayDeviceId);
		when(gatewayDeviceMock.getName()).thenReturn(gatewayDeviceName);
		when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayDeviceMock);

		// Action
		ReflectionTestUtils.invokeMethod(platformSubscriber, "subscribeGatewayDeviceOperation");

		verify(operationNotificationSubscriberMock, times(1)).subscribe(eq(gatewayDeviceId),
				subscriptionListenerCaptor.capture());
		assertNotNull(ReflectionTestUtils.getField(platformSubscriber, "subscriberForOperationsOnGateway"));

		// when onNotification
		OperationRepresentation operationRepresentation = new OperationRepresentation();
		subscriptionListenerCaptor.getValue().onNotification(new Subscription<GId>() {
			@Override
			public GId getObject() {
				return gatewayDeviceId;
			}

			@Override
			public void unsubscribe() {
			}
		}, operationRepresentation);

		verify(eventPublisher, times(1)).publishEvent(receivedOperationForGatewayEventCaptor.capture());

		assertEquals(gatewayDeviceId, receivedOperationForGatewayEventCaptor.getValue().getDeviceId());
		assertEquals(gatewayDeviceName, receivedOperationForGatewayEventCaptor.getValue().getDeviceName());
		assertEquals(operationRepresentation,
				receivedOperationForGatewayEventCaptor.getValue().getOperationRepresentation());
	}

	@Test
	public void shouldNotPublishReceivedOperationForGatewayEventWhenDeviceIDsDoNotMatch() {
		final GId gatewayDeviceId = GId.asGId("111");
		String gatewayDeviceName = "snmp-agent-test";

		OperationNotificationSubscriber operationNotificationSubscriberMock = mock(
				OperationNotificationSubscriber.class);
		when(deviceControlApi.getNotificationsSubscriber()).thenReturn(operationNotificationSubscriberMock);

		// Inject the gatewayDevice mock
		GatewayManagedObjectWrapper gatewayDeviceMock = mock(GatewayManagedObjectWrapper.class);
		when(gatewayDeviceMock.getId()).thenReturn(gatewayDeviceId);
		when(gatewayDeviceMock.getName()).thenReturn(gatewayDeviceName);
		when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayDeviceMock);

		// Action
		ReflectionTestUtils.invokeMethod(platformSubscriber, "subscribeGatewayDeviceOperation");

		verify(operationNotificationSubscriberMock, times(1)).subscribe(eq(gatewayDeviceId),
				subscriptionListenerCaptor.capture());
		assertNotNull(ReflectionTestUtils.getField(platformSubscriber, "subscriberForOperationsOnGateway"));

		// when onNotification
		OperationRepresentation operationRepresentation = new OperationRepresentation();
		subscriptionListenerCaptor.getValue().onNotification(new Subscription<GId>() {
			@Override
			public GId getObject() {
				return GId.asGId("222"); // Different ID
			}

			@Override
			public void unsubscribe() {
			}
		}, operationRepresentation);

		verifyZeroInteractions(eventPublisher);

		// when onError
		subscriptionListenerCaptor.getValue().onError(new Subscription<GId>() {
			@Override
			public GId getObject() {
				return GId.asGId("222"); // Different ID
			}

			@Override
			public void unsubscribe() {
			}
		}, new Exception("SOME EXCEPTION"));

		verifyZeroInteractions(eventPublisher);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldKeepSubscriberForOperationsForGatewayDeviceWhenSubscribeThrowsException() {
		final GId gatewayDeviceId = GId.asGId("111");
		String gatewayDeviceName = "snmp-agent-test";

		OperationNotificationSubscriber operationNotificationSubscriberMock = mock(
				OperationNotificationSubscriber.class);
		when(deviceControlApi.getNotificationsSubscriber()).thenReturn(operationNotificationSubscriberMock);

		// Inject the gatewayDevice mock
		GatewayManagedObjectWrapper gatewayDeviceMock = mock(GatewayManagedObjectWrapper.class);
		when(gatewayDeviceMock.getId()).thenReturn(gatewayDeviceId);
		when(gatewayDeviceMock.getName()).thenReturn(gatewayDeviceName);
		when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayDeviceMock);
		when(operationNotificationSubscriberMock.subscribe(eq(gatewayDeviceId), any(SubscriptionListener.class)))
				.thenThrow(new SDKException("SOME ERROR"));

		// Action
		ReflectionTestUtils.invokeMethod(platformSubscriber, "subscribeGatewayDeviceOperation");

		verify(operationNotificationSubscriberMock, times(1)).subscribe(eq(gatewayDeviceId),
				subscriptionListenerCaptor.capture());
		assertNull(ReflectionTestUtils.getField(platformSubscriber, "subscriberForOperationsOnGateway"));
	}

	@Test
	public void shouldNotSubscribeForGatewayInventoryNotificationSecondTime() {
		InventoryRealtimeDeleteAwareNotificationsSubscriber subscriberMock = mock(
				InventoryRealtimeDeleteAwareNotificationsSubscriber.class);
		ReflectionTestUtils.setField(platformSubscriber, "gatewayNotificationSubscriber", subscriberMock);

		// Action
		ReflectionTestUtils.invokeMethod(platformSubscriber, "subscribeGatewayInventoryNotification");

		verifyZeroInteractions(gatewayDataProvider);
	}

	@Test
	public void shouldNotSubscribeForOperationsForGatewayDeviceSecondTime() {
		OperationNotificationSubscriber operationNotificationSubscriberMock = mock(
				OperationNotificationSubscriber.class);

		ReflectionTestUtils.setField(platformSubscriber, "subscriberForOperationsOnGateway",
				operationNotificationSubscriberMock);

		// Action
		ReflectionTestUtils.invokeMethod(platformSubscriber, "subscribeGatewayDeviceOperation");

		verifyZeroInteractions(operationNotificationSubscriberMock);
		assertNotNull(ReflectionTestUtils.getField(platformSubscriber, "subscriberForOperationsOnGateway"));
	}
}
