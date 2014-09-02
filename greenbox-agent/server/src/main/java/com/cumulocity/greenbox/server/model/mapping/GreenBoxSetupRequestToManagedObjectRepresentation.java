package com.cumulocity.greenbox.server.model.mapping;

import org.modelmapper.PropertyMap;

import c8y.IsDevice;

import com.cumulocity.greenbox.server.model.GreenBoxSetupRequest;
import com.cumulocity.model.Agent;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

public class GreenBoxSetupRequestToManagedObjectRepresentation extends PropertyMap<GreenBoxSetupRequest, ManagedObjectRepresentation> {

    @Override
    protected void configure() {
        destination.setType("c8y_greenbox_Agent");
        destination.set(new Agent());
        destination.set(new IsDevice());
        destination.setName(source.getHub());
        
    }

}
