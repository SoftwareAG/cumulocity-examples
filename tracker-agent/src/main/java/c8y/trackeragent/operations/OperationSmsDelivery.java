package c8y.trackeragent.operations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sms.gateway.client.OutgoingMessagingClient;
import com.cumulocity.sms.gateway.model.Address;
import com.cumulocity.sms.gateway.model.outgoing.OutgoingMessageRequest;
import com.cumulocity.sms.gateway.model.outgoing.SendMessageRequest;

import c8y.CommunicationMode;
import c8y.Mobile;
import c8y.trackeragent.device.TrackerDevice;

import static com.cumulocity.sms.gateway.model.Address.phoneNumber;

@Component
public class OperationSmsDelivery {

    private final InventoryApi inventoryApi;
    private final OutgoingMessagingClient outgoingMessagingClient;

    @Autowired
    public OperationSmsDelivery (InventoryApi inventoryApi, OutgoingMessagingClient outgoingMessagingClient) {
        this.inventoryApi = inventoryApi;
        this.outgoingMessagingClient = outgoingMessagingClient;
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
    public boolean isSmsMode (OperationRepresentation operation, TrackerDevice device) {
        
        if (operation.get("deliveryType") != null && operation.get("deliveryType").equals("SMS")) {
            return true;
        } 

        ManagedObjectRepresentation deviceMo = inventoryApi.get(operation.getDeviceId());
        CommunicationMode communicationMode = deviceMo.get(CommunicationMode.class);

        if (communicationMode != null && communicationMode.getMode().toLowerCase().equals("sms")) {
            return true;
        }
        
        return false;
    }

    public String getProvider(String tenant) {
      //todo get provider from file
        return "telia";
    }
    
    public void deliverSms (String translation, GId deviceId) throws IllegalArgumentException {

        ManagedObjectRepresentation deviceMo = inventoryApi.get(deviceId);
        Mobile mobile = deviceMo.get(Mobile.class);
        String receiver = mobile.getMsisdn();
        if (receiver == null || receiver.length() == 0) {
            throw new IllegalArgumentException("MSISDN of target device cannot be null");
        }
        Address address = phoneNumber(receiver);
        String sender = "";
        SendMessageRequest request = SendMessageRequest.builder().withReceiver(address).withSender(address).withMessage(translation).build();

        MultiValueMap<String, String> additionalHeaders = new LinkedMultiValueMap<String, String>();
        
        additionalHeaders.add("provider", getProvider(""));
        outgoingMessagingClient.send(address, additionalHeaders, new OutgoingMessageRequest(request));
    }
}
