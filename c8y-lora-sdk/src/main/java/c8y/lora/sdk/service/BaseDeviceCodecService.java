package c8y.lora.sdk.service;

import c8y.lora.sdk.model.DeviceCodecRepresentation;
import com.cumulocity.lpwan.codec.model.Decode;
import com.cumulocity.lpwan.codec.model.Encode;
import com.cumulocity.lpwan.devicetype.model.DecodedDataMapping;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionRemovedEvent;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class BaseDeviceCodecService implements CodecServiceMetadata {

    @Autowired
    InventoryApi inventoryApi;

    ManagedObjectRepresentation codecMo;

    final String CODEC_TYPE = "Device Codec";

    public abstract List<DecodedDataMapping> decode(Decode decode);

    public abstract String encode(Encode encode);

    public abstract List<String> getModels();

    public abstract JSONObject getMetData();

    @EventListener
    private void registerCodec(MicroserviceSubscriptionAddedEvent event) {
        ManagedObjectRepresentation mor = new ManagedObjectRepresentation();
        mor.set(new DeviceCodecRepresentation());
        mor.setType(CODEC_TYPE);
        mor.setName(getName());
        codecMo = inventoryApi.create(mor);
    }

    @EventListener
    private void deregister(MicroserviceSubscriptionRemovedEvent event){
        inventoryApi.delete(codecMo.getId());
    }
}
