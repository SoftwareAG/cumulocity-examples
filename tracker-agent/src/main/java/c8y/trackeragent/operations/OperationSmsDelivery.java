package c8y.trackeragent.operations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.sms.Address;
import com.cumulocity.model.sms.SendMessageRequest;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;

import c8y.CommunicationMode;
import c8y.Mobile;
import c8y.trackeragent.device.TrackerDevice;

import static com.cumulocity.model.sms.Address.phoneNumber;

@Component
public class OperationSmsDelivery {

    private final InventoryApi inventoryApi;
    
    @Autowired
    public OperationSmsDelivery (InventoryApi inventoryApi) {
        this.inventoryApi = inventoryApi;
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

    public void getProvider(String tenant) {
        
        
    }
    
    public void deliverSms (String translation, GId deviceId) {

        ManagedObjectRepresentation deviceMo = inventoryApi.get(deviceId);
        Mobile mobile = deviceMo.get(Mobile.class);
        String receiver = mobile.getMsisdn();
        if (receiver == "" || receiver.length() == 0) {
            throw new IllegalArgumentException("MSISDN of target device cannot be null");
        }
        Address address = phoneNumber(receiver);
        String sender = "";
        SendMessageRequest request = SendMessageRequest.builder()
                .withReceiver(address)
                .withSender(address)
                .withMessage(translation).build();
    }
}
