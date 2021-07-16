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
import c8y.Mobile;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.devicebootstrap.MicroserviceSubscriptionsServiceWrapper;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.sms.Address;
import com.cumulocity.model.sms.SendMessageRequest;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sms.client.SmsMessagingApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.cumulocity.model.sms.Address.phoneNumber;

@Slf4j
@Component
public class OperationSmsDelivery {

    private final InventoryApi inventoryApi;
    private final SmsMessagingApi outgoingMessagingClient;

    private final DeviceCredentialsRepository deviceCredentials;
    private final MicroserviceSubscriptionsServiceWrapper microserviceSubscriptionsServiceWrapper;

    @Autowired
    public OperationSmsDelivery (
            InventoryApi inventoryApi,
            SmsMessagingApi outgoingMessagingClient,
            DeviceCredentialsRepository deviceCredentials,
            MicroserviceSubscriptionsServiceWrapper microserviceSubscriptionsServiceWrapper) {
        this.inventoryApi = inventoryApi;
        this.outgoingMessagingClient = outgoingMessagingClient;
        this.deviceCredentials = deviceCredentials;
        this.microserviceSubscriptionsServiceWrapper = microserviceSubscriptionsServiceWrapper;
    }
    
    /**
     * return Operation delivery mode based on "Device control via SMS" chapter link: http://cumulocity.com/guides/reference/device-control/
     * "c8y_CommunicationMode": {
        "mode": "SMS"
        }
        or
        "deliveryType": "SMS"
     * @param operation
     * @return operation delivery Mode
     */
    public boolean isSmsMode (OperationRepresentation operation) {
        
        if ("sms".equalsIgnoreCase(String.valueOf(operation.get("deliveryType")))) {
            return true;
        } 

        ManagedObjectRepresentation deviceMo = inventoryApi.get(operation.getDeviceId());
        CommunicationMode communicationMode = deviceMo.get(CommunicationMode.class);

        if (communicationMode != null && "sms".equalsIgnoreCase(communicationMode.getMode())) {
            return true;
        }
        
        return false;
    }

    public void deliverSms(String translation, GId deviceId, String imei) throws IllegalArgumentException {
        log.debug("sending sms {} {} {}", translation, deviceId, imei);

        ManagedObjectRepresentation deviceMo = inventoryApi.get(deviceId);
        Mobile mobile = deviceMo.get(Mobile.class);
        String receiver = mobile.getMsisdn();

        if (receiver == null || receiver.length() == 0) {
            throw new IllegalArgumentException("MSISDN of target device cannot be null");
        }

        final Address address = phoneNumber(receiver);
        final SendMessageRequest request = SendMessageRequest.builder().withReceiver(address).withSender(address).withMessage(translation).build();

        final DeviceCredentials device = this.deviceCredentials.getDeviceCredentials(imei);
        microserviceSubscriptionsServiceWrapper.runForTenant(device.getTenant(), () -> {
            outgoingMessagingClient.sendMessage(request);
        });
    }
}
