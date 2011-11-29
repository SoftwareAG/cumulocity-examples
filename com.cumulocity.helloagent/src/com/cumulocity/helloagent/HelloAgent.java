package com.cumulocity.helloagent;

import java.math.BigDecimal;
import java.util.Date;

import org.osgi.framework.FrameworkListener;

import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.ClientException;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.ResourceInternalException;
import com.cumulocity.sdk.client.inventory.InventoryResource;
import com.cumulocity.sdk.client.measurement.MeasurementApiResource;
import com.cumulocity.sdk.client.platform.PlatformImpl;

public class HelloAgent implements Runnable {

    private static final String CUMULOCITY_HOST = "http://developer.cumulocity.com";

    private static final int CUMULOCITY_PORT = 80;

    private static final String TENANT = "...";

    private static final String USER = "...";

    private static final String PASS = "...";

    private FrameworkListener listener;

    public HelloAgent(FrameworkListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {

        // Wait for all OSGi bundles to become active
        try {
            synchronized (listener) {
                listener.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create a platform client
        Platform platform = new PlatformImpl(CUMULOCITY_HOST + ":" + CUMULOCITY_PORT, TENANT, USER, PASS);
        
        // Retrieve the Resource for the Inventory and Measurement
        InventoryResource inventoryResource = platform.getInventory();
        MeasurementApiResource measurementResource = platform.getMeasurement();

        try {

            // Create a ManagedObjectRepresentation for our object and set some properties
            ManagedObjectRepresentation mor = new ManagedObjectRepresentation();
            mor.setName("Hello World!");

            // Create the object in the database (update the local representation
            // with the data added by the database)
            mor = inventoryResource.getMOCollectionResource().create(mor);

			// Create a MeasurementRepresentation for our object and set some properties
			MeasurementRepresentation measurementRepresentation = new MeasurementRepresentation();
			measurementRepresentation.setSource(mor);
			measurementRepresentation.setTime(new Date());
			measurementRepresentation.setType("Hello World Measurement");
			
			MeasurementValue measurement = new MeasurementValue();
			measurement.setQuantity("UNIX time");
			measurement.setUnit("ms");
			measurement.setValue(new BigDecimal(System.currentTimeMillis()));
			
			measurementRepresentation.set(measurement, "My measurement");
			measurementResource.getMeasurementCollectionResource().create(measurementRepresentation);

        } catch (ResourceInternalException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ClientException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
