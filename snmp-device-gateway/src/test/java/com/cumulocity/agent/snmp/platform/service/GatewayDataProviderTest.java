package com.cumulocity.agent.snmp.platform.service;

import com.cumulocity.agent.snmp.bootstrap.model.BootstrapReadyEvent;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.GatewayDataRefreshedEvent;
import com.cumulocity.agent.snmp.utils.Constants;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceCollectionRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;

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
public class GatewayDataProviderTest {

	@Mock
	private InventoryApi inventoryApi;

	@Mock
	private TaskScheduler taskScheduler;

	@Mock
	private GatewayProperties properties;

	@Mock
	private PlatformProvider platformProvider;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Spy
	@InjectMocks
	GatewayDataProvider gatewayDataprovider;

	@Test
	public void shouldUpdateGatewayObjectOnBootstrapReadyEvent() {
		ManagedObjectReferenceCollectionRepresentation childDevices = new ManagedObjectReferenceCollectionRepresentation();
		ManagedObjectRepresentation gatewayDeviceMo = new ManagedObjectRepresentation();
		gatewayDeviceMo.setId(new GId("snmp-agent"));
		gatewayDeviceMo.setChildDevices(childDevices);
		BootstrapReadyEvent event = new BootstrapReadyEvent(gatewayDeviceMo);

		when(inventoryApi.get(any())).thenReturn(gatewayDeviceMo);

		gatewayDataprovider.refreshGatewayObjects(event);

		verify(gatewayDataprovider).updateGatewayObjects();
		verify(eventPublisher).publishEvent(any(GatewayDataRefreshedEvent.class));
	}

	@Test
	public void shouldScheduleGatewayDataRefreshOnBootstrapReadyEvent() {
		ManagedObjectReferenceCollectionRepresentation childDevices = new ManagedObjectReferenceCollectionRepresentation();
		ManagedObjectRepresentation gatewayDeviceMo = new ManagedObjectRepresentation();
		gatewayDeviceMo.setId(new GId("snmp-agent"));
		gatewayDeviceMo.setChildDevices(childDevices);
		BootstrapReadyEvent event = new BootstrapReadyEvent(gatewayDeviceMo);

		when(properties.getGatewayObjectRefreshIntervalInMinutes()).thenReturn(2);
		when(inventoryApi.get(any())).thenReturn(gatewayDeviceMo);

		gatewayDataprovider.refreshGatewayObjects(event);

		verify(gatewayDataprovider, times(1)).scheduleGatewayDataRefresh();
		verify(taskScheduler).scheduleWithFixedDelay(any(Runnable.class), eq(Duration.ofMinutes(2)));
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

		childDeviceMo.set(propertiesMap, Constants.C8Y_SNMP_DEVICE);

		List<ManagedObjectReferenceRepresentation> childDeviceRefList = new ArrayList<>();
		childDeviceRefList.add(childDeviceRef);
		childDevices.setReferences(childDeviceRefList);

		gatewayDeviceMo.setChildDevices(childDevices);

		ManagedObjectRepresentation deviceProtocolMo = new ManagedObjectRepresentation();
		deviceProtocolMo.setId(new GId("device-protocol"));

		BootstrapReadyEvent event = new BootstrapReadyEvent(gatewayDeviceMo);

		when(inventoryApi.get(new GId("snmp-agent"))).thenReturn(gatewayDeviceMo);
		when(inventoryApi.get(new GId("child-device"))).thenReturn(childDeviceMo);
		when(inventoryApi.get(new GId("device-protocol"))).thenReturn(deviceProtocolMo);

		assertNull(gatewayDataprovider.getGatewayDevice());
		assertEquals(gatewayDataprovider.getCurrentDeviceProtocolMap().size(), 0);

		gatewayDataprovider.refreshGatewayObjects(event);

		assertNotNull(gatewayDataprovider.getGatewayDevice());
		assertEquals(gatewayDataprovider.getCurrentDeviceProtocolMap().size(), 1);
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

		childDeviceMo.set(propertiesMap, Constants.C8Y_SNMP_DEVICE);

		List<ManagedObjectReferenceRepresentation> childDeviceRefList = new ArrayList<>();
		childDeviceRefList.add(childDeviceRef);
		childDevices.setReferences(childDeviceRefList);

		gatewayDeviceMo.setChildDevices(childDevices);

		ManagedObjectRepresentation deviceProtocolMo = new ManagedObjectRepresentation();
		deviceProtocolMo.setId(new GId("device-protocol"));

		BootstrapReadyEvent event = new BootstrapReadyEvent(gatewayDeviceMo);

		when(inventoryApi.get(new GId("snmp-agent"))).thenReturn(gatewayDeviceMo);
		when(inventoryApi.get(new GId("child-device"))).thenReturn(childDeviceMo);
		when(inventoryApi.get(new GId("device-protocol"))).thenThrow(new SDKException(HttpStatus.SC_NOT_FOUND, "Object Not found"));

		assertNull(gatewayDataprovider.getGatewayDevice());
		assertEquals(gatewayDataprovider.getCurrentDeviceProtocolMap().size(), 0);

		gatewayDataprovider.refreshGatewayObjects(event);

		assertEquals(gatewayDataprovider.getCurrentDeviceProtocolMap().size(), 0);
	}
}
