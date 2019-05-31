package com.cumulocity.snmp.service.client;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.factory.gateway.DeviceFactory;
import com.cumulocity.snmp.model.device.DeviceAddedEvent;
import com.cumulocity.snmp.model.device.DeviceRemovedEvent;
import com.cumulocity.snmp.model.device.DeviceUpdatedEvent;
import com.cumulocity.snmp.model.gateway.*;
import com.cumulocity.snmp.model.gateway.client.ClientDataChangedEvent;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.gateway.type.core.Register;
import com.cumulocity.snmp.model.type.DeviceType;
import com.cumulocity.snmp.repository.DeviceTypeInventoryRepository;
import com.cumulocity.snmp.repository.ManagedObjectRepository;
import com.cumulocity.snmp.repository.core.Repository;
import com.cumulocity.snmp.utils.gateway.Scheduler;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.snmp4j.smi.VariableBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cumulocity.snmp.model.core.ConfigEventType.NO_REGISTERS;
import static com.cumulocity.snmp.model.core.ConfigEventType.URL;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Slf4j
@Service
public class ClientSubscriber {

    @Autowired
    private Repository<DeviceType> deviceTypeRepository;

    @Autowired
    private Repository<Device> deviceRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DeviceInterface deviceInterface;

    @Autowired
    private DevicePollingService pollingService;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private DeviceFactory deviceFactory;

    @Autowired
    private ManagedObjectRepository managedObjectRepository;

    @Autowired
    private DeviceTypeInventoryRepository deviceTypeInventoryRepository;

    private ScheduledFuture<?> future = null;
    private Gateway gateway = null;
    private AtomicInteger currentPollingRateInSeconds = new AtomicInteger(0);

    private Map<String, Map<String, PduListener>> mapIPAddressToOid = new ConcurrentHashMap<>();
    private Map<String, List<Register>> mapIpAddressToRegister = new ConcurrentHashMap<>();
    private Map<Device, DeviceType> devicePollingData = new ConcurrentHashMap<>();

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscription(final DeviceTypeAddedEvent event) {
        log.debug("Initiating DeviceType add");
        Device device = event.getDevice();
        this.gateway = event.getGateway();
        subscribe(device, event.getDeviceType());
    }

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscription(final DeviceTypeUpdatedEvent event) {
        log.debug("Initiating DeviceType update");
        this.gateway = event.getGateway();
        List<Device> devices = getDevicesOfDeviceType(event.getDevice().getDeviceType());
        for (Device deviceOfGivenDeviceType : devices) {
            subscribe(deviceOfGivenDeviceType, event.getDeviceType());
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscription(final DeviceTypeRemovedEvent event) {
        log.debug("Initiating DeviceType remove");
        List<Device> devices = getDevicesOfDeviceType(event.getDeviceTypeId());
        for (Device device : devices) {
            mapIPAddressToOid.remove(device.getIpAddress());
            mapIpAddressToRegister.remove(device.getIpAddress());
            deviceInterface.removeOidMappings(device.getIpAddress());
            devicePollingData.remove(device);
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscription(final DeviceAddedEvent event) {
        log.debug("Initiating Device add");
        this.gateway = event.getGateway();
        if (event.getDevice().getDeviceType() != null) {
            final Optional<DeviceType> deviceTypeOptional =
                    deviceTypeInventoryRepository.get(gateway, event.getDevice().getDeviceType());
            if (deviceTypeOptional.isPresent()) {
                final Device device = event.getDevice();
                subscribe(device, deviceTypeOptional.get());
            }
        }
    }

    @EventListener
    @RunWithinContext
    public void updateDevice(final DeviceUpdatedEvent event) {
        this.gateway = event.getGateway();
        final Optional<ManagedObjectRepresentation> optional = managedObjectRepository.get(gateway, event.getDeviceId());
        if (optional.isPresent()) {
            final Optional<Device> deviceOptional = deviceFactory.convert(optional.get());
            if (deviceOptional.isPresent()) {
                final Optional<DeviceType> deviceTypeOptional =
                        deviceTypeInventoryRepository.get(gateway, deviceOptional.get().getDeviceType());
                subscribe(deviceOptional.get(), deviceTypeOptional.get());
            }
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void unsubscribe(final DeviceRemovedEvent event) {
        log.debug("Initiating Device remove");
        unsubscribe(event.getDevice());
    }

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscriptions(final GatewayAddedEvent event) {
        log.debug("Initiating Gateway add");
        this.gateway = event.getGateway();
        try {
            handleScheduler();
            updateSubscriptions();
        } catch (final Exception ex) {
            log.error("Failed to add gateway");
            log.error(ex.getMessage(), ex);
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscriptions(final GatewayUpdateEvent event) {
        log.debug("Initiating Gateway update");
        this.gateway = event.getGateway();
        try {
            handleScheduler();
            updateSubscriptions();
        } catch (final Exception ex) {
            log.error("Failed to update gateway");
            log.error(ex.getMessage(), ex);
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void unsubscribe(final GatewayRemovedEvent event) {
        log.debug("Initiating Gateway delete");
        terminateSchedulerIfRunning();
        devicePollingData.clear();
        mapIPAddressToOid.clear();
        mapIpAddressToRegister.clear();
        this.gateway = null;
    }

    private void handleScheduler() {
        if (isChildDevicesAvailable() && isPollingRateAvailable()) {
            refreshScheduler();
        } else {
            terminateSchedulerIfRunning();
        }
    }

    private boolean isPollingRateAvailable() {
        return this.gateway.getPollingRateInSeconds() != 0;
    }

    private void updateSubscriptions() {
        log.debug("Updating Device Subscription");
        deviceInterface.setGateway(gateway);
        final List<GId> currentDeviceIds = gateway.getCurrentDeviceIds();
        if (currentDeviceIds != null) {
            for (final GId gId : currentDeviceIds) {
                final Optional<Device> deviceOptional = deviceRepository.get(gId);
                if (deviceOptional.isPresent()) {
                    final Device device = deviceOptional.get();
                    if (device.getDeviceType() != null) {
                        final Optional<DeviceType> deviceTypeOptional =
                                deviceTypeInventoryRepository.get(gateway, deviceOptional.get().getDeviceType());
                        if (deviceTypeOptional.isPresent()) {
                            log.debug("Adding/Updating device details for polling");
                            devicePollingData.put(device, deviceTypeOptional.get());
                        }
                    }
                }
            }
            if (gateway.getPollingRateInSeconds() == 0) {
                log.debug("Device polling will be scheduled once polling rate is found");
                pollDevices();
            }
            subscribe();
        }
    }

    private void pollDevices() {
        for (Device device : devicePollingData.keySet()) {
            pollDevice(gateway, device);
        }
    }

    private void pollDevice(final Gateway gateway, final Device device) {
        List<Register> registers = mapIpAddressToRegister.get(device.getIpAddress());
        if (registers != null) {
            for (final Register register : registers) {
                if (register.getMeasurementMapping() != null) {
                    pollingService.initiatePolling(register.getOid(), device, new PduListener() {
                        @Override
                        public void onVariableBindingReceived(VariableBinding variableBinding) {
                            eventPublisher.publishEvent(new ClientDataChangedEvent(gateway, device, register,
                                    new DateTime(), variableBinding, true));
                        }
                    });
                }
            }
        } else {
            log.debug("No data mappings found for device ", device.getIpAddress());
        }
    }

    private void subscribe() {
        for (Map.Entry<Device, DeviceType> deviceData : devicePollingData.entrySet()) {
            if (deviceData.getValue() != null) {
                subscribe(deviceData.getKey(), deviceData.getValue());
            }
        }
    }

    private void subscribe(final Device device, DeviceType deviceType) {
        try {
            if (!validRegisters(gateway, device, deviceType)) {
                return;
            }

            Map<String, PduListener> mapOidToPduListener = new HashMap();
            clearGatewayValidationErrors(gateway);
            deviceInterface.setGateway(gateway);
            for (final Register register : deviceType.getRegisters()) {
                mapOidToPduListener.put(register.getOid(), new PduListener() {
                    @Override
                    public void onVariableBindingReceived(VariableBinding variableBinding) {
                        eventPublisher.publishEvent(new ClientDataChangedEvent(gateway, device, register,
                                new DateTime(), variableBinding, false));
                    }
                });
            }
            mapIPAddressToOid.put(device.getIpAddress(), mapOidToPduListener);
            mapIpAddressToRegister.put(device.getIpAddress(), deviceType.getRegisters());
            deviceInterface.subscribe(mapIPAddressToOid);
            devicePollingData.remove(device);
            devicePollingData.put(device, deviceType);
        } catch (final Exception ex) {
            log.error("Failed to subscribe device configuration mapping");
            log.error(ex.getMessage(), ex);
        }
    }

    private boolean validRegisters(Gateway gateway, Device device, DeviceType deviceType) {
        boolean valid = isNotEmpty(deviceType.getRegisters());
        if (!valid) {
            eventPublisher.publishEvent(new DeviceConfigErrorEvent(gateway, device, null, NO_REGISTERS));
        }
        return valid;
    }

    private void clearGatewayValidationErrors(Gateway gateway) {
        eventPublisher.publishEvent(new GatewayConfigSuccessEvent(gateway, URL));
    }

    private void unsubscribe(Device device) {
        log.debug("Device unsubscribed");
        List<Device> devices = getDevicesOfDeviceType(device.getDeviceType());
        for (Device deviceOfType : devices) {
            deviceInterface.unsubscribe(deviceOfType.getIpAddress());
            mapIPAddressToOid.remove(deviceOfType.getIpAddress());
            mapIpAddressToRegister.remove(deviceOfType.getIpAddress());
            devicePollingData.remove(deviceOfType);
        }
        if (!isChildDevicesAvailable()) {
            terminateSchedulerIfRunning();
        }
    }

    private void refreshScheduler() {
        log.debug("Scheduling Device Polling on user defined interval if polling rate exists");
        if (gateway != null && (gateway.getPollingRateInSeconds() > 0)
                && (!isSchedulerRunning() || (currentPollingRateInSeconds.get() != gateway.getPollingRateInSeconds()))) {
            terminateSchedulerIfRunning();
            currentPollingRateInSeconds.set(gateway.getPollingRateInSeconds());
            future = scheduler.scheduleWithFixedDelay(new Runnable() {
                public void run() {
                    log.debug("Running scheduled device polling");
                    pollDevices();
                }
            }, 1000 * currentPollingRateInSeconds.get());
        }
    }

    private boolean isChildDevicesAvailable() {
        List<GId> childDevices = gateway.getCurrentDeviceIds();
        return (childDevices != null) && (childDevices.size() > 0);
    }

    private void terminateSchedulerIfRunning() {
        log.debug("Deleting Task if exists");
        if (future != null && (!future.isCancelled())) {
            future.cancel(true);
        }
    }

    private boolean isSchedulerRunning() {
        return future != null && (!future.isCancelled());
    }

    private List<Device> getDevicesOfDeviceType(GId deviceTypeGId) {
        List<Device> devices = new ArrayList<>();
        for (Map.Entry<Device, DeviceType> data : devicePollingData.entrySet()) {
            if (data.getValue().getId().equals(deviceTypeGId)) {
                devices.add(data.getKey());
            }
        }
        return devices;
    }
}