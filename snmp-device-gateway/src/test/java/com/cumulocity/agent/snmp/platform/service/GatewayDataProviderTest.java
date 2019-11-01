package com.cumulocity.agent.snmp.platform.service;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.GatewayDataRefreshedEvent;
import com.cumulocity.agent.snmp.platform.model.GatewayManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.ReceivedOperationForGatewayEvent;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceCollectionRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.devicecontrol.notification.OperationNotificationSubscriber;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class GatewayDataProviderTest {

	@Mock
	private InventoryApi inventoryApi;

	@Mock
	private DeviceControlApi deviceControlApi;

	@Captor
	private ArgumentCaptor<SubscriptionListener<GId, OperationRepresentation>> subscriptionListenerCaptor;

	@Captor
	private ArgumentCaptor<ReceivedOperationForGatewayEvent> receivedOperationForGatewayEventCaptor;

	@Mock
	private GatewayProperties properties;

	@Mock
	private PlatformProvider platformProvider;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Spy
	private ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

	@Spy
	@InjectMocks
	GatewayDataProvider gatewayDataProvider;

	@Before
	public void setup() {
		taskScheduler.initialize();
	}

	@Test
	public void shouldScheduleGatewayDataRefreshOnUpdateGatewayObjects() {
		ManagedObjectReferenceCollectionRepresentation childDevices = new ManagedObjectReferenceCollectionRepresentation();
		ManagedObjectRepresentation gatewayDeviceMo = new ManagedObjectRepresentation();
		gatewayDeviceMo.setId(new GId("snmp-agent"));
		gatewayDeviceMo.setChildDevices(childDevices);

		when(properties.getGatewayObjectRefreshIntervalInMinutes()).thenReturn(2);
		when(inventoryApi.get(any())).thenReturn(gatewayDeviceMo);

		OperationNotificationSubscriber operationNotificationSubscriberMock = mock(OperationNotificationSubscriber.class);
		when(deviceControlApi.getNotificationsSubscriber()).thenReturn(operationNotificationSubscriberMock);

		gatewayDataProvider.updateGatewayObjects(gatewayDeviceMo);

		verify(gatewayDataProvider, times(1)).scheduleGatewayDataRefresh();
		verify(taskScheduler).scheduleWithFixedDelay(any(Runnable.class), eq(Duration.ofMinutes(2)));

		verify(operationNotificationSubscriberMock, times(1)).subscribe(eq(gatewayDataProvider.getGatewayDevice().getId()), any(SubscriptionListener.class));
		assertNotNull(ReflectionTestUtils.getField(gatewayDataProvider, "subscriberForOperationsOnGateway"));
	}

	@Test
	public void shouldRefreshGatewayObjectsOnUpdateGatewayObjects() {
		ManagedObjectRepresentation gatewayDeviceMo = new ManagedObjectRepresentation();
		gatewayDeviceMo.setId(new GId("snmp-agent"));

		ManagedObjectRepresentation childDeviceMo = new ManagedObjectRepresentation();
		childDeviceMo.setId(new GId("child-device"));

		ManagedObjectReferenceCollectionRepresentation childDevices = new ManagedObjectReferenceCollectionRepresentation();
		ManagedObjectReferenceRepresentation childDeviceRef = new ManagedObjectReferenceRepresentation();
		childDeviceRef.setManagedObject(childDeviceMo);

		Map<String, Object> propertiesMap = new HashMap<>();
		propertiesMap.put("version", 0);
		propertiesMap.put("port", "161");
		propertiesMap.put("type", "/inventory/managedObjects/device-protocol");
		propertiesMap.put("ipAddress", "127.0.0.1");

		childDeviceMo.set(propertiesMap, DeviceManagedObjectWrapper.C8Y_SNMP_DEVICE);

		List<ManagedObjectReferenceRepresentation> childDeviceRefList = new ArrayList<>();
		childDeviceRefList.add(childDeviceRef);
		childDevices.setReferences(childDeviceRefList);

		gatewayDeviceMo.setChildDevices(childDevices);

		ManagedObjectRepresentation deviceProtocolMo = new ManagedObjectRepresentation();
		deviceProtocolMo.setId(new GId("device-protocol"));

		when(inventoryApi.get(new GId("snmp-agent"))).thenReturn(gatewayDeviceMo);
		when(inventoryApi.get(new GId("child-device"))).thenReturn(childDeviceMo);
		when(inventoryApi.get(new GId("device-protocol"))).thenReturn(deviceProtocolMo);
		when(properties.getGatewayObjectRefreshIntervalInMinutes()).thenReturn(1);

		OperationNotificationSubscriber operationNotificationSubscriberMock = mock(OperationNotificationSubscriber.class);
		when(deviceControlApi.getNotificationsSubscriber()).thenReturn(operationNotificationSubscriberMock);

		assertNull(gatewayDataProvider.getGatewayDevice());
		assertEquals(gatewayDataProvider.getSnmpDeviceMap().size(), 0);

		gatewayDataProvider.updateGatewayObjects(gatewayDeviceMo);

		assertNotNull(gatewayDataProvider.getGatewayDevice());
		assertEquals(gatewayDataProvider.getSnmpDeviceMap().size(), 1);

		verify(operationNotificationSubscriberMock, times(1)).subscribe(eq(gatewayDataProvider.getGatewayDevice().getId()), any(SubscriptionListener.class));
		assertNotNull(ReflectionTestUtils.getField(gatewayDataProvider, "subscriberForOperationsOnGateway"));
	}

	@Test
	public void shouldNotUpdateDeviceProtocolIfNotPresentInPlatform() {
		ManagedObjectRepresentation gatewayDeviceMo = new ManagedObjectRepresentation();
		gatewayDeviceMo.setId(new GId("snmp-agent"));

		ManagedObjectRepresentation childDeviceMo = new ManagedObjectRepresentation();
		childDeviceMo.setId(new GId("child-device"));

		ManagedObjectReferenceCollectionRepresentation childDevices = new ManagedObjectReferenceCollectionRepresentation();
		ManagedObjectReferenceRepresentation childDeviceRef = new ManagedObjectReferenceRepresentation();
		childDeviceRef.setManagedObject(childDeviceMo);

		Map<String, Object> propertiesMap = new HashMap<>();
		propertiesMap.put("version", 0);
		propertiesMap.put("port", "161");
		propertiesMap.put("type", "/inventory/managedObjects/device-protocol");
		propertiesMap.put("ipAddress", "127.0.0.1");

		childDeviceMo.set(propertiesMap, DeviceManagedObjectWrapper.C8Y_SNMP_DEVICE);

		List<ManagedObjectReferenceRepresentation> childDeviceRefList = new ArrayList<>();
		childDeviceRefList.add(childDeviceRef);
		childDevices.setReferences(childDeviceRefList);

		gatewayDeviceMo.setChildDevices(childDevices);

		when(inventoryApi.get(new GId("snmp-agent"))).thenReturn(gatewayDeviceMo);
		when(inventoryApi.get(new GId("child-device"))).thenReturn(childDeviceMo);
		when(inventoryApi.get(new GId("device-protocol")))
				.thenThrow(new SDKException(HttpStatus.SC_NOT_FOUND, "Object Not found"));
		when(properties.getGatewayObjectRefreshIntervalInMinutes()).thenReturn(1);

		OperationNotificationSubscriber operationNotificationSubscriberMock = mock(OperationNotificationSubscriber.class);
		when(deviceControlApi.getNotificationsSubscriber()).thenReturn(operationNotificationSubscriberMock);

		assertNull(gatewayDataProvider.getGatewayDevice());
		assertEquals(gatewayDataProvider.getSnmpDeviceMap().size(), 0);

		gatewayDataProvider.updateGatewayObjects(gatewayDeviceMo);

		assertEquals(1, gatewayDataProvider.getSnmpDeviceMap().size());
		assertNull(gatewayDataProvider.getProtocolMap().get("device-protocol"));
		verify(operationNotificationSubscriberMock, times(1)).subscribe(eq(gatewayDataProvider.getGatewayDevice().getId()), any(SubscriptionListener.class));
		assertNotNull(ReflectionTestUtils.getField(gatewayDataProvider, "subscriberForOperationsOnGateway"));
	}

	@Test
	public void shouldGenerateGatewayDataRefreshedEventOnRefreshGatewayObjects() throws InterruptedException {
		ManagedObjectRepresentation gatewayDeviceMo = new ManagedObjectRepresentation();
		gatewayDeviceMo.setId(new GId("snmp-agent"));

		ManagedObjectReferenceCollectionRepresentation childDevices = new ManagedObjectReferenceCollectionRepresentation();
		ManagedObjectReferenceRepresentation childDeviceRef = new ManagedObjectReferenceRepresentation();
		ManagedObjectRepresentation childDeviceMo = new ManagedObjectRepresentation();
		childDeviceMo.setId(new GId("child-device"));

		childDeviceRef.setManagedObject(childDeviceMo);
		gatewayDeviceMo.setChildDevices(childDevices);

		when(properties.getGatewayObjectRefreshIntervalInMinutes()).thenReturn(1);
		when(platformProvider.isPlatformAvailable()).thenReturn(true);

		doAnswer((Answer<Void>) invocation -> {
			return null;
		}).when(gatewayDataProvider).refreshGatewayObjects();

		gatewayDataProvider.scheduleGatewayDataRefresh();
		Thread.sleep(1000);

		verify(gatewayDataProvider).refreshGatewayObjects();
		verify(eventPublisher).publishEvent(any(GatewayDataRefreshedEvent.class));
	}

	@Test
	public void shouldPublishReceivedOperationForGatewayEventWhenDeviceIDsMatch() {
		final GId gatewayDeviceId = GId.asGId("111");
		String gatewayDeviceName = "snmp-agent-test";

		OperationNotificationSubscriber operationNotificationSubscriberMock = mock(OperationNotificationSubscriber.class);
		when(deviceControlApi.getNotificationsSubscriber()).thenReturn(operationNotificationSubscriberMock);

		// Inject the gatewayDevice mock
		GatewayManagedObjectWrapper gatewayDeviceMock = mock(GatewayManagedObjectWrapper.class);
		when(gatewayDeviceMock.getId()).thenReturn(gatewayDeviceId);
		when(gatewayDeviceMock.getName()).thenReturn(gatewayDeviceName);
		ReflectionTestUtils.setField(gatewayDataProvider, "gatewayDevice", gatewayDeviceMock);

		// when
		ReflectionTestUtils.invokeMethod(gatewayDataProvider, "subscribeForOperationsForGatewayDevice");

		verify(operationNotificationSubscriberMock, times(1)).subscribe(eq(gatewayDeviceId), subscriptionListenerCaptor.capture());
		assertNotNull(ReflectionTestUtils.getField(gatewayDataProvider, "subscriberForOperationsOnGateway"));

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
		assertEquals(operationRepresentation, receivedOperationForGatewayEventCaptor.getValue().getOperationRepresentation());
	}

	@Test
	public void shouldNotPublishReceivedOperationForGatewayEventWhenDeviceIDsDoNotMatch() {
		final GId gatewayDeviceId = GId.asGId("111");
		String gatewayDeviceName = "snmp-agent-test";

		OperationNotificationSubscriber operationNotificationSubscriberMock = mock(OperationNotificationSubscriber.class);
		when(deviceControlApi.getNotificationsSubscriber()).thenReturn(operationNotificationSubscriberMock);

		// Inject the gatewayDevice mock
		GatewayManagedObjectWrapper gatewayDeviceMock = mock(GatewayManagedObjectWrapper.class);
		when(gatewayDeviceMock.getId()).thenReturn(gatewayDeviceId);
		when(gatewayDeviceMock.getName()).thenReturn(gatewayDeviceName);
		ReflectionTestUtils.setField(gatewayDataProvider, "gatewayDevice", gatewayDeviceMock);

		// when
		ReflectionTestUtils.invokeMethod(gatewayDataProvider, "subscribeForOperationsForGatewayDevice");

		verify(operationNotificationSubscriberMock, times(1)).subscribe(eq(gatewayDeviceId), subscriptionListenerCaptor.capture());
		assertNotNull(ReflectionTestUtils.getField(gatewayDataProvider, "subscriberForOperationsOnGateway"));

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
	public void shouldNotSubscribeForOperationsForGatewayDeviceSecondTime() {
		OperationNotificationSubscriber operationNotificationSubscriberMock = mock(OperationNotificationSubscriber.class);

		ReflectionTestUtils.setField(gatewayDataProvider, "subscriberForOperationsOnGateway", operationNotificationSubscriberMock);

		// When
		ReflectionTestUtils.invokeMethod(gatewayDataProvider, "subscribeForOperationsForGatewayDevice");

		verifyZeroInteractions(operationNotificationSubscriberMock);
		assertNotNull(ReflectionTestUtils.getField(gatewayDataProvider, "subscriberForOperationsOnGateway"));
	}

	@Test
	public void shouldKeepSubscriberForOperationsForGatewayDeviceWhenSubscribeThrowsException() {
		final GId gatewayDeviceId = GId.asGId("111");
		String gatewayDeviceName = "snmp-agent-test";

		OperationNotificationSubscriber operationNotificationSubscriberMock = mock(OperationNotificationSubscriber.class);
		when(deviceControlApi.getNotificationsSubscriber()).thenReturn(operationNotificationSubscriberMock);

		// Inject the gatewayDevice mock
		GatewayManagedObjectWrapper gatewayDeviceMock = mock(GatewayManagedObjectWrapper.class);
		when(gatewayDeviceMock.getId()).thenReturn(gatewayDeviceId);
		when(gatewayDeviceMock.getName()).thenReturn(gatewayDeviceName);
		ReflectionTestUtils.setField(gatewayDataProvider, "gatewayDevice", gatewayDeviceMock);

		when(operationNotificationSubscriberMock.subscribe(eq(gatewayDeviceId), any(SubscriptionListener.class))).thenThrow(new SDKException("SOME ERROR"));

		// when
		ReflectionTestUtils.invokeMethod(gatewayDataProvider, "subscribeForOperationsForGatewayDevice");

		verify(operationNotificationSubscriberMock, times(1)).subscribe(eq(gatewayDeviceId), subscriptionListenerCaptor.capture());
		assertNull(ReflectionTestUtils.getField(gatewayDataProvider, "subscriberForOperationsOnGateway"));
	}
}
