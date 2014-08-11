package com.cumulocity.tixi.server.model;

import c8y.IsDevice;
import c8y.RequiredAvailability;
import c8y.SupportedOperations;

import com.cumulocity.model.Agent;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

public class ManagedObjects {

    public static ManagedObjectRepresentation asManagedObject(GId id) {
        final ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.setId(id);
        return managedObjectRepresentation;
    }
    
    public static ManagedObjectRepresentation tixiAgentManagedObject(String serialNumber) {
        ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.setName("Tixi Modem " + serialNumber);
        managedObjectRepresentation.setType("c8y_TixiAgent");
        managedObjectRepresentation.set(new Agent());
        managedObjectRepresentation.set(new IsDevice());
        managedObjectRepresentation.set(new RequiredAvailability(15));
        SupportedOperations supportedOperations = new SupportedOperations();
        supportedOperations.add("c8y_MeasurementRequestOperation");
        managedObjectRepresentation.set(supportedOperations);
        return managedObjectRepresentation;
    }
    
    public static ManagedObjectRepresentation deviceManagedObject(String name, String type) {
        ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.setName(name);
        managedObjectRepresentation.setType("tixi_device");
        return managedObjectRepresentation;
    }
    
    public static ManagedObjectRepresentation agentManagedObject(String name) {
        ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.setName(name);
        managedObjectRepresentation.setType(name);
        managedObjectRepresentation.set(new Agent());
        managedObjectRepresentation.set(new IsDevice());
        return managedObjectRepresentation;
    }
}
