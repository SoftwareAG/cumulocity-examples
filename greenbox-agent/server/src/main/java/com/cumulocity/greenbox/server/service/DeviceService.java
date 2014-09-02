package com.cumulocity.greenbox.server.service;

import static com.cumulocity.greenbox.server.model.HubUid.asHubUid;
import static com.cumulocity.rest.representation.inventory.ManagedObjects.asManagedObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.IsDevice;
import c8y.RequiredAvailability;

import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.agent.server.repository.MeasurementRepository;
import com.cumulocity.greenbox.server.model.*;
import com.cumulocity.model.Agent;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.SDKException;

@Component
public class DeviceService {

    private static final int REQUIRED_INTERVAL = 30;//min

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    public void setup(GreenBoxSetupRequest message) {
        try {
            final ManagedObjectRepresentation agent = inventoryRepository.findByExternalId(asHubUid(message.getHubUID()));
        } catch (SDKException ex) {
            initialize(message);
        }
    }

    private void initialize(GreenBoxSetupRequest message) {
        ManagedObjectRepresentation agent = toAgent(message);
        final MeasurementDefinitionRegistry dataPoints = new MeasurementDefinitionRegistry();
        agent.set(dataPoints);
        for (DataPoint dataPoint : message.getDataPoints()) {
            dataPoints.save(dataPoint.getDataPointId(), asFragment(dataPoint));
        }
        agent = inventoryRepository.save(agent, asHubUid(message.getHubUID()));
    }

    private MeasurementDefinition asFragment(DataPoint dataPoint) {
        return new MeasurementDefinition(dataPoint.getName(), dataPoint.getUnit());
    }

    private ManagedObjectRepresentation toDevice(Device device) {
        ManagedObjectRepresentation representation = new ManagedObjectRepresentation();
        representation.setType("c8y_greenbox_Device");
        representation.setName(device.getName());
        representation.set(device.getType(), "type_name");
        representation.set(device.getAddress(), "address");
        representation.set(device.getType(), "type");
        return representation;
    }

    private ManagedObjectRepresentation toAgent(GreenBoxSetupRequest message) {
        ManagedObjectRepresentation representation = new ManagedObjectRepresentation();
        representation.setType("c8y_greenbox_Hub");
        representation.set(new Agent());
        representation.set(new IsDevice());
        representation.setName(message.getHub());
        representation.set(message.getIpaddresses(), "ipAddresses");
        representation.set(new RequiredAvailability(REQUIRED_INTERVAL));
        return representation;
    }

    public void send(GreenBoxSendRequest message) {
        final ManagedObjectRepresentation agent = inventoryRepository.findByExternalId(asHubUid(message.getHubUID()));
        MeasurementDefinitionRegistry registry = agent.get(MeasurementDefinitionRegistry.class);
        for (Measurement data : message.getData()) {
            MeasurementRepresentation measurement = new MeasurementRepresentation();
            measurement.setTime(data.getTime().toDate());
            measurement.setSource(asManagedObject(agent.getId()));
            measurement.setType("c8y_greenbox_Measurment");
            for (MeasurementEntry value : data.getDataPoints()) {
                final MeasurementDefinition measurementDefinition = registry.get(value.getId());
                measurement.set(measurementDefinition.toMeasurementValue(value), "c8y_" + measurementDefinition.getName());
            }
            measurementRepository.save(measurement);
        }
    }

}
