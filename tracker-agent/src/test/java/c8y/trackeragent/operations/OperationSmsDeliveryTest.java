package c8y.trackeragent.operations;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.sms.gateway.model.Address;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sms.gateway.client.OutgoingMessagingClient;
import com.cumulocity.sms.gateway.model.outgoing.OutgoingMessageRequest;
import com.cumulocity.sms.gateway.model.outgoing.SendMessageRequest;

import c8y.CommunicationMode;
import c8y.Mobile;
import c8y.trackeragent.configuration.TrackerConfiguration;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.sms.OptionsAuthorizationSupplier;
import c8y.trackeragent.operations.OperationSmsDelivery;

import static com.cumulocity.sms.gateway.model.Address.phoneNumber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class OperationSmsDeliveryTest {

    private final InventoryApi inventoryApi = mock(InventoryApi.class);
    private final OutgoingMessagingClient outgoingMessagingClient = mock(OutgoingMessagingClient.class);
    private final TrackerConfiguration config = mock(TrackerConfiguration.class);
    private final DeviceCredentialsRepository deviceCredentialsRepo = mock(DeviceCredentialsRepository.class);
    private final OptionsAuthorizationSupplier optionsAuthSupplier = mock(OptionsAuthorizationSupplier.class);
    
    private OperationSmsDelivery operationSmsDelivery;
    String imei = "12345";
    String tenant = "tenant";
    String msisdn = "123";
    String translation = "text-to-tracker";
    GId deviceId;
    ManagedObjectRepresentation managedObject;
    DeviceCredentials deviceCredentials;
    ArgumentCaptor<OutgoingMessageRequest> messageRequestCaptor = ArgumentCaptor.forClass(OutgoingMessageRequest.class);
    ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
    
    @Before
    public void setup() {
        
        operationSmsDelivery = new OperationSmsDelivery(inventoryApi, outgoingMessagingClient, config, deviceCredentialsRepo, optionsAuthSupplier);
        
        managedObject = new ManagedObjectRepresentation();
        Mobile mobile = new Mobile();
        managedObject.set(mobile);
        CommunicationMode communicationMode = new CommunicationMode();
        managedObject.set(communicationMode);
        when(inventoryApi.get(any(GId.class))).thenReturn(managedObject);
        
        deviceId = new GId("1");
        deviceCredentials = DeviceCredentials.forDevice(imei, tenant);
        when(deviceCredentialsRepo.getDeviceCredentials(imei)).thenReturn(deviceCredentials);
        
    
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
        setupMOtoDeliverSms();
        Address address = phoneNumber(msisdn);
        SendMessageRequest request = SendMessageRequest.builder().withReceiver(address).withSender(address).withMessage(translation).build();
        operationSmsDelivery.deliverSms(translation, deviceId, imei);
        
        verify(outgoingMessagingClient).send(addressCaptor.capture(), messageRequestCaptor.capture());
        assertEquals(request.toString(), messageRequestCaptor.getValue().getOutboundSMSMessageRequest().toString());
        assertEquals(address, addressCaptor.getValue());
        
        verify(optionsAuthSupplier).optionsAuthForTenant(config, tenant);
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
