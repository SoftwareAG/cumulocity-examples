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
import c8y.trackeragent.devicebootstrap.MicroserviceSubscriptionsServiceWrapper;
import c8y.trackeragent.devicemapping.DeviceTenantMappingService;
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

    private final DeviceTenantMappingService deviceTenantMappingService;
    private final MicroserviceSubscriptionsServiceWrapper microserviceSubscriptionsService;

    @Autowired
    public OperationSmsDelivery (
            InventoryApi inventoryApi,
            SmsMessagingApi outgoingMessagingClient,
            DeviceTenantMappingService deviceTenantMappingService,
            MicroserviceSubscriptionsServiceWrapper microserviceSubscriptionsServiceWrapper) {
        this.inventoryApi = inventoryApi;
        this.outgoingMessagingClient = outgoingMessagingClient;
        this.deviceTenantMappingService = deviceTenantMappingService;
        this.microserviceSubscriptionsService = microserviceSubscriptionsServiceWrapper;
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

        String tenant = deviceTenantMappingService.findTenant(imei);
        microserviceSubscriptionsService.runForTenant(tenant, () -> {
            outgoingMessagingClient.sendMessage(request);
        });
    }
}
