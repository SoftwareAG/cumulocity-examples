/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.operations;

import c8y.CommunicationMode;
import c8y.MicroserviceSubscriptionsServiceMock;
import c8y.Mobile;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.devicebootstrap.MicroserviceSubscriptionsServiceWrapper;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import c8y.trackeragent.devicemapping.DeviceTenantMappingService;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.sms.Address;
import com.cumulocity.model.sms.SendMessageRequest;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sms.client.SmsMessagingApi;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.Optional;

import static com.cumulocity.model.sms.Address.phoneNumber;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class OperationSmsDeliveryTest {

    private final InventoryApi inventoryApi = mock(InventoryApi.class);
    private final SmsMessagingApi outgoingMessagingClient = mock(SmsMessagingApi.class);
    private final DeviceCredentialsRepository deviceCredentialsRepo = mock(DeviceCredentialsRepository.class);
    private final MicroserviceSubscriptionsServiceMock microserviceSubscriptionsService = new MicroserviceSubscriptionsServiceMock();
    private final DeviceTenantMappingService deviceTenantMappingService = mock(DeviceTenantMappingService.class);
    private OperationSmsDelivery operationSmsDelivery;

    String imei = "12345";
    String msisdn = "123";
    String translation = "text-to-tracker";
    String tenant = "tenant";
    GId deviceId;
    ManagedObjectRepresentation managedObject;
    DeviceCredentials deviceCredentials = DeviceCredentials.forDevice(imei, tenant);
    ArgumentCaptor<SendMessageRequest> messageRequestCaptor = ArgumentCaptor.forClass(SendMessageRequest.class);

    @Before
    public void setup() {

        operationSmsDelivery = new OperationSmsDelivery(
                inventoryApi,
                outgoingMessagingClient,
                deviceTenantMappingService,
                new MicroserviceSubscriptionsServiceWrapper(microserviceSubscriptionsService));
        
        managedObject = new ManagedObjectRepresentation();
        Mobile mobile = new Mobile();
        managedObject.set(mobile);
        CommunicationMode communicationMode = new CommunicationMode();
        managedObject.set(communicationMode);
        when(inventoryApi.get(any(GId.class))).thenReturn(managedObject);
        
        deviceId = new GId("1");
        when(deviceTenantMappingService.findTenant(imei)).thenReturn(tenant);
    }
    
    public void setupMOtoDeliverSms() {
        Mobile mobile = new Mobile();
        mobile.setMsisdn(msisdn);
        managedObject.set(mobile);
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenMsisdnNotExist() {
        operationSmsDelivery.deliverSms(translation, deviceId, imei);
    }
    
    @Test
    public void shouldSendSmsToMsisdn() {
        //given
        microserviceSubscriptionsService.setOptionalMicroserviceCredentials(Map.of(
                tenant, Optional.of(new MicroserviceCredentials())
        ));
        setupMOtoDeliverSms();
        Address address = phoneNumber(msisdn);
        SendMessageRequest request = SendMessageRequest.builder().withReceiver(address).withSender(address).withMessage(translation).build();

        //when
        operationSmsDelivery.deliverSms(translation, deviceId, imei);

        //then
        verify(outgoingMessagingClient).sendMessage(messageRequestCaptor.capture());
        assertEquals(request.toString(), messageRequestCaptor.getValue().toString());
        verify(outgoingMessagingClient).sendMessage(any());
    }
    
    @Test
    public void shouldConfirmSmsModeFromOperation() {
        OperationRepresentation operation = new OperationRepresentation();
        operation.setProperty("deliveryType", "SMS");
        boolean result = operationSmsDelivery.isSmsMode(operation);
        assertEquals(true, result);
        
        operation.setProperty("deliveryType", "Sms");
        result = operationSmsDelivery.isSmsMode(operation);
        assertEquals(true, result);
    }
    
    @Test
    public void shouldConfirmSmsModeFromDeviceMO() {
        OperationRepresentation operation = new OperationRepresentation();
        operation.setDeviceId(deviceId);
        
        CommunicationMode communicationMode = new CommunicationMode("SMS");
        managedObject.set(communicationMode);
        boolean result = operationSmsDelivery.isSmsMode(operation);
        assertEquals(true, result);
        
        communicationMode.setMode("sMs");
        result = operationSmsDelivery.isSmsMode(operation);
        assertEquals(true, result);
        
        
    }
    
    @Test
    public void shouldDenySmsMode() {
        OperationRepresentation operation = new OperationRepresentation();
        operation.setDeviceId(deviceId);
        
        boolean result = operationSmsDelivery.isSmsMode(operation);
        assertEquals(false, result);
        
        operation.setProperty("deliveryType", "xyz");
        result = operationSmsDelivery.isSmsMode(operation);
        assertEquals(false, result);
        
        CommunicationMode communicationMode = new CommunicationMode("xyz");
        managedObject.set(communicationMode);
        result = operationSmsDelivery.isSmsMode(operation);
        assertEquals(false, result);
    }
}
