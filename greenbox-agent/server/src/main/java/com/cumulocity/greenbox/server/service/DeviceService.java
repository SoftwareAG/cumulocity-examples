package com.cumulocity.greenbox.server.service;

import static com.cumulocity.agent.server.repository.ManagedObjects.fromManagedObjectReference;
import static com.cumulocity.agent.server.repository.ManagedObjects.nameEquals;
import static com.cumulocity.greenbox.server.model.HubUid.asHubUid;
import static com.cumulocity.model.event.CumulocityAlarmStatuses.ACTIVE;
import static com.cumulocity.model.event.CumulocitySeverities.CRITICAL;
import static com.cumulocity.rest.representation.inventory.ManagedObjects.asManagedObject;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.FluentIterable.from;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import c8y.IsDevice;
import c8y.RequiredAvailability;

import com.cumulocity.agent.server.repository.AlarmRepository;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.agent.server.repository.MeasurementRepository;
import com.cumulocity.greenbox.server.model.*;
import com.cumulocity.model.Agent;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;

@Component
public class DeviceService {

    private static final Logger log = LoggerFactory.getLogger(DeviceService.class);

    private static final int REQUIRED_INTERVAL = 24 * 60;//min

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private List<MeasurementValuePostProcessor> measurementPostProcessors;

    @Autowired
    private AlarmRepository alarmRepository;

    public void setup(final GreenBoxSetupRequest message) {
        ManagedObjectRepresentation agent = findAgent(message).or(createAgent(message));
        updateInventory(message, agent);
    }

    private Supplier<ManagedObjectRepresentation> createAgent(final GreenBoxSetupRequest message) {
        return new Supplier<ManagedObjectRepresentation>() {
            @Override
            public ManagedObjectRepresentation get() {
                log.info("registering new GreenBoxAgent {}", message.getHubUID());
                return toAgent(message);
            }
        };
    }

    private void updateInventory(GreenBoxSetupRequest message, ManagedObjectRepresentation agent) {
        if (isShouldUpdateInventory(agent, message)) {
            final Map<String, ManagedObjectRepresentation> devicesByGeenboxId = groupChildDevicesByGeenboxId(message, agent);
            agent = asManagedObject(agent.getId());
            agent.set(createDataPointRegistry(message.getDataPoints(), devicesByGeenboxId));
            save(agent);
        } else {
            log.info("inventory not changed for {}", agent.getId());
        }
    }

    private Map<String, ManagedObjectRepresentation> groupChildDevicesByGeenboxId(GreenBoxSetupRequest message,
            ManagedObjectRepresentation agent) {
        final Map<String, ManagedObjectRepresentation> devicesByGeenboxId = new HashMap<>();
        FluentIterable<ManagedObjectRepresentation> childDevices = from(getChildDevices(agent));

        for (final Device greenboxDevice : message.getDevices()) {
            final ManagedObjectRepresentation device = findByName(childDevices, greenboxDevice.getName()).or(
                    createChildDevice(agent, greenboxDevice));
            devicesByGeenboxId.put(greenboxDevice.getId(), device);
        }
        return devicesByGeenboxId;
    }

    private ManagedObjectRepresentation save(ManagedObjectRepresentation managedObject) {
        return inventoryRepository.save(managedObject);
    }

    private boolean isShouldUpdateInventory(ManagedObjectRepresentation agent, GreenBoxSetupRequest message) {
        return isChildDevicesChanged(agent, message) || isDataPointRegistryChanged(agent, message);
    }

    private Iterable<ManagedObjectRepresentation> getChildDevices(ManagedObjectRepresentation agent) {
        return from(agent.getChildDevices()).transform(fromManagedObjectReference()).toList();
    }

    private Optional<ManagedObjectRepresentation> findByName(FluentIterable<ManagedObjectRepresentation> childDevices, final String name) {
        return childDevices.firstMatch(nameEquals(name));
    }

    private boolean isDataPointRegistryChanged(ManagedObjectRepresentation agent, GreenBoxSetupRequest message) {
        return agent.get(DataPointRegistry.class).size() != message.getDataPoints().size();
    }

    private Supplier<ManagedObjectRepresentation> createChildDevice(final ManagedObjectRepresentation agent, final Device greenboxDevice) {
        return new Supplier<ManagedObjectRepresentation>() {
            @Override
            public ManagedObjectRepresentation get() {
                log.info("registering new device for {} with name {}", agent.getId(), greenboxDevice.getName());
                final ManagedObjectRepresentation child = toDevice(greenboxDevice);
                inventoryRepository.bindToParent(agent.getId(), child.getId());
                return child;
            }
        };
    }

    private boolean isChildDevicesChanged(final ManagedObjectRepresentation agent, GreenBoxSetupRequest message) {
        return agent.getChildDevices().getReferences().size() != message.getDevices().size();
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
        inventoryRepository.save(asManagedObject(id));
    }

    private ManagedObjectRepresentation toAgent(GreenBoxSetupRequest message) {
        ManagedObjectRepresentation representation = new ManagedObjectRepresentation();
        representation.setType("c8y_greenbox_Hub");
        representation.set(new Agent());
        representation.set(new IsDevice());
        representation.setName(message.getHub());
        representation.set(message.getIpaddresses(), "ipAddresses");
        representation.set(new RequiredAvailability(REQUIRED_INTERVAL));
        return save(representation);
    }

    public void send(GreenBoxSendRequest message) {
        final ManagedObjectRepresentation agent = findAgent(message).get();
        final DataPointRegistry registry = fromNullable(agent.get(DataPointRegistry.class)).or(new DataPointRegistry());
        final UniqueMeasurmentsFactory measurements = new UniqueMeasurmentsFactory();
        for (Measurement data : message.getData()) {
            for (MeasurementEntry value : data.getDataPoints()) {
                final DataPoint dataPoint = registry.get(value.getId());
                verifyDataPoint(agent, value, dataPoint);
                MeasurementRepresentation measurement = measurements.get(bySourceAndTime(dataPoint.getDeviceId(), data.getTime()));
                measurement.set(asMeasurementFragment(value, dataPoint), "c8y_" + dataPoint.getName());
            }
        }
        ping(agent.getId());
        measurementRepository.save(measurements.getMeasurments());
    }

    private Map<String, MeasurementValue> asMeasurementFragment(MeasurementEntry value, final DataPoint dataPoint) {
        return singletonMap(dataPoint.getName(), newMeasurementValue(value, dataPoint));
    }

    private MeasurementWithSourceAndTime bySourceAndTime(final String source, final DateTime time) {
        return new MeasurementWithSourceAndTime(source, time);
    }

    private void verifyDataPoint(final ManagedObjectRepresentation agent, MeasurementEntry value, final DataPoint dataPoint) {
        if (dataPoint == null) {
            riseAlarm(agent, value.getId());
            throw new IllegalStateException(format("Data point with id %s is not registed", value.getId()));
        }
    }

    private Optional<ManagedObjectRepresentation> findAgent(GreenBoxRequest message) {
        try {
            return Optional.of(inventoryRepository.findByExternalId(asHubUid(message.getHubUID())));
        } catch (SDKException ex) {
            if (ex.getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
                log.info("agnet with given UID not found {}", message.getHubUID());
                return Optional.absent();
            } else {
                throw propagate(ex);
            }
        }
    }

    private void riseAlarm(ManagedObjectRepresentation agent, String id) {
        AlarmRepresentation alarm = new AlarmRepresentation();
        alarm.setType("c8y_greenbox_DataPointNotRegistred");
        alarm.setSource(asManagedObject(agent.getId()));
        alarm.setStatus(ACTIVE.name());
        alarm.setSeverity(CRITICAL.name());
        alarm.setTime(new Date());
        alarm.setText(format("Data point with id %s is not registed", id));
        alarmRepository.save(alarm);
    }

    private MeasurementValue newMeasurementValue(MeasurementEntry entry, final DataPoint dataPoint) {
        MeasurementValue value = new MeasurementValue(entry.getValue(), dataPoint.getUnit(), null, null, null);
        for (MeasurementValuePostProcessor measurementValuePostProcessor : measurementPostProcessors) {
            value = measurementValuePostProcessor.process(value);
        }
        return value;
    }

}
