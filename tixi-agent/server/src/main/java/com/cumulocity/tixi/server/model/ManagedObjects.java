package com.cumulocity.tixi.server.model;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

public class ManagedObjects {

    public static ManagedObjectRepresentation asManagedObject(GId id) {
        final ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.setId(id);
        return managedObjectRepresentation;
    }
}
