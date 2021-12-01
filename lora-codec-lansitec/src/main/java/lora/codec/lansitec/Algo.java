package lora.codec.lansitec;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class Algo {
    @Autowired
    protected MeasurementApi measurementApi;

    @Autowired
    protected InventoryApi inventoryApi;

    public abstract String getId();

    public abstract String getLabel();

    public abstract Beacon getPosition(ManagedObjectRepresentation tracker, List<Beacon> beacons);
}
