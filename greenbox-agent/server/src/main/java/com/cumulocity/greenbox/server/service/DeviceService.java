package com.cumulocity.greenbox.server.service;

import static com.cumulocity.agent.server.repository.ManagedObjects.*;
import static com.cumulocity.greenbox.server.model.HubUid.asHubUid;
import static com.cumulocity.rest.representation.inventory.ManagedObjects.asManagedObject;
import static com.google.common.collect.FluentIterable.from;

import java.util.*;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.IsDevice;
import c8y.RequiredAvailability;

import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.agent.server.repository.MeasurementRepository;
import com.cumulocity.greenbox.server.model.*;
import com.cumulocity.model.Agent;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjects;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;

@Component
public class DeviceService {

    private static final int REQUIRED_INTERVAL = 24 * 60;//min

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private List<MeasurementValuePostProcessor> measurementPostProcessors;

    public void setup(GreenBoxSetupRequest message) {
        ManagedObjectRepresentation agent = null;
        try {
            agent = inventoryRepository.findByExternalId(asHubUid(message.getHubUID()));
        } catch (SDKException ex) {
            initializeInventory(message);
        }
        if (agent != null) {
            updateInventory(message, agent);
        }
    }

    private void updateInventory(GreenBoxSetupRequest message, ManagedObjectRepresentation agent) {
        if (isChildDevicesChanged(agent, message) || isDataPointRegistryChanged(agent, message)) {
            final Map<String, ManagedObjectRepresentation> devices = new HashMap<>();
            FluentIterable<ManagedObjectRepresentation> childDevices = from(from(agent.getChildDevices()).transform(
                    fromManagedObjectReference()).toList());

            for (final Device greenboxDevice : message.getDevices()) {
                final ManagedObjectRepresentation device = childDevices.firstMatch(nameEquals(greenboxDevice.getName())).or(
                        createChild(agent, greenboxDevice));
                devices.put(greenboxDevice.getId(), device);
            }
            agent = asManagedObject(agent.getId());
            agent.set(createDataPointRegistry(message.getDataPoints(), devices));
            inventoryRepository.save(agent);
        }
    }

    private boolean isDataPointRegistryChanged(ManagedObjectRepresentation agent, GreenBoxSetupRequest message) {
        return agent.get(DataPointRegistry.class).size() != message.getDataPoints().size();
    }

    private Supplier<ManagedObjectRepresentation> createChild(final ManagedObjectRepresentation agent, final Device greenboxDevice) {
        return new Supplier<ManagedObjectRepresentation>() {
            @Override
            public ManagedObjectRepresentation get() {
                final ManagedObjectRepresentation child = toDevice(greenboxDevice);
                inventoryRepository.bindToParent(agent.getId(), child.getId());
                return child;
            }
        };
    }

    private boolean isChildDevicesChanged(final ManagedObjectRepresentation agent, GreenBoxSetupRequest message) {
        return agent.getChildDevices().getReferences().size() != message.getDevices().size();
    }

    private void initializeInventory(GreenBoxSetupRequest message) {
        ManagedObjectRepresentation agent = toAgent(message);

        final Map<String, ManagedObjectRepresentation> devices = new HashMap<>();
        for (Device device : message.getDevices()) {
            devices.put(device.getId(), toDevice(device));
        }
        agent.set(createDataPointRegistry(message.getDataPoints(), devices));
        agent = inventoryRepository.save(agent, asHubUid(message.getHubUID()));
        for (ManagedObjectRepresentation device : devices.values()) {
            inventoryRepository.bindToParent(agent.getId(), device.getId());
        }

    }

    private DataPointRegistry createDataPointRegistry(Iterable<DataPoint> dataPoints, final Map<String, ManagedObjectRepresentation> devices) {
        final DataPointRegistry registry = new DataPointRegistry();
        for (DataPoint dataPoint : dataPoints) {
            final ManagedObjectRepresentation device = devices.get(dataPoint.getDeviceId());
            dataPoint.setDeviceId(GId.asString(device.getId()));
            registry.save(dataPoint.getDataPointId(), dataPoint);
        }
        return registry;
    }

    private ManagedObjectRepresentation toDevice(Device device) {
        ManagedObjectRepresentation representation = new ManagedObjectRepresentation();
        representation.setName(device.getName());
        representation.set(device.getDescription(), "c8y_Notes");
        representation.set(device.getType(), "type");
        return inventoryRepository.save(representation);
    }

    private void ping(GId id) {
        inventoryRepository.save(ManagedObjects.asManagedObject(id));
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
        ping(agent.getId());
        final DataPointRegistry registry = agent.get(DataPointRegistry.class);
        final Measurements measurements = new Measurements();
        for (Measurement data : message.getData()) {
            for (MeasurementEntry value : data.getDataPoints()) {
                final DataPoint dataPoint = registry.get(value.getId());
                MeasurementRepresentation measurement = measurements.get(new MeasurementId(dataPoint.getDeviceId(), data.getTime()));
                measurement.set(Collections.singletonMap(dataPoint.getName(), newMeasurementValue(value, dataPoint)), dataPoint.getName());
            }
        }
        measurementRepository.save(measurements.getMeasurments());
    }

    private MeasurementValue newMeasurementValue(MeasurementEntry entry, final DataPoint dataPoint) {

        MeasurementValue value = new MeasurementValue(entry.getValue(), dataPoint.getUnit(), null, null, null);
        for (MeasurementValuePostProcessor measurementValuePostProcessor : measurementPostProcessors) {
            value = measurementValuePostProcessor.process(value);
        }
        return value;
    }

    class MeasurementId {
        private final String source;

        private final DateTime time;

        public MeasurementId(String source, DateTime time) {
            this.source = source;
            this.time = time;
        }

        public String getSource() {
            return source;
        }

        public DateTime getTime() {
            return time;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((source == null) ? 0 : source.hashCode());
            result = prime * result + ((time == null) ? 0 : time.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MeasurementId other = (MeasurementId) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (source == null) {
                if (other.source != null)
                    return false;
            } else if (!source.equals(other.source))
                return false;
            if (time == null) {
                if (other.time != null)
                    return false;
            } else if (!time.equals(other.time))
                return false;
            return true;
        }

        private DeviceService getOuterType() {
            return DeviceService.this;
        }

    }

    class Measurements {

        private final Map<MeasurementId, MeasurementRepresentation> storage = new HashMap<MeasurementId, MeasurementRepresentation>();

        public MeasurementRepresentation get(MeasurementId id) {
            if (!storage.containsKey(id)) {
                MeasurementRepresentation newMeasurement = new MeasurementRepresentation();
                newMeasurement.setTime(id.getTime().toDate());
                newMeasurement.setSource(asManagedObject(GId.asGId(id.getSource())));
                newMeasurement.setType("c8y_greenbox_Measurment");
                storage.put(id, newMeasurement);
            }
            return storage.get(id);
        }

        public Collection<MeasurementRepresentation> getMeasurments() {
            return storage.values();
        }

    }

}
