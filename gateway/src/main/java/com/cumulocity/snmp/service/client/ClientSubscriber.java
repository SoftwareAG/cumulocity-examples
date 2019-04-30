package com.cumulocity.snmp.service.client;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.factory.gateway.DeviceFactory;
import com.cumulocity.snmp.model.core.ConfigEventType;
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
import org.snmp4j.PDU;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    Repository<DeviceType> deviceTypeRepository;

    @Autowired
    Repository<Device> deviceRepository;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    DeviceInterface deviceInterface;

    @Autowired
    DevicePollingService pollingService;

    @Autowired
    Scheduler scheduler;

    @Autowired
    DeviceFactory deviceFactory;

    @Autowired
    ManagedObjectRepository managedObjectRepository;

    @Autowired
    DeviceTypeInventoryRepository deviceTypeInventoryRepository;

    ScheduledFuture<?> future = null;
    Gateway gateway = null;
    AtomicInteger currentPollingRateInSeconds = new AtomicInteger(0);

    Map<String, Map<String, PduListener>> mapIPAddressToOid = new ConcurrentHashMap<>();
    Map<String, List<Register>> mapIpAddressToRegister = new ConcurrentHashMap<>();
    Map<Device, DeviceType> devicePollingData = new ConcurrentHashMap<>();

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscription(final DeviceTypeAddedEvent event) {
        log.debug("Initiating DeviceType add");
        final Device device = event.getDevice();
        this.gateway = event.getGateway();
        subscribe(device, event.getDeviceType());
    }

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscription(final DeviceTypeUpdatedEvent event) {
        log.debug("Initiating DeviceType update");
        this.gateway = event.getGateway();
        final Device device = event.getDevice();
        subscribe(device, event.getDeviceType());
    }

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscription(final DeviceAddedEvent event) {
        log.debug("Initiating Device add");
        this.gateway = event.getGateway();
        if (event.getDevice().getDeviceType() != null) {
            final Optional<DeviceType> deviceTypeOptional = deviceTypeRepository.get(event.getDevice().getDeviceType());
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
                final Optional<DeviceType> deviceTypeOptional = deviceTypeInventoryRepository.get(gateway, deviceOptional.get().getDeviceType());
                subscribe(deviceOptional.get(), deviceTypeOptional.get());
            }
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscriptions(final GatewayAddedEvent event) {
        log.debug("Initiating Gateway add");
        this.gateway = event.getGateway();
        try {
            refreshScheduler();
            updateSubscriptions();
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            eventPublisher.publishEvent(new GatewayConfigErrorEvent(gateway, new ConfigEventType(ex.getMessage())));
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscriptions(final GatewayUpdateEvent event) {
        log.debug("Initiating Gateway update");
        this.gateway = event.getGateway();
        try {
            refreshScheduler();
            updateSubscriptions();
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
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
    public synchronized void unsubscribe(final GatewayRemovedEvent event) {
        log.debug("Initiating Gateway delete");
        terminateTaskIfRunning();
        devicePollingData.clear();
        mapIPAddressToOid.clear();
        mapIpAddressToRegister.clear();
        this.gateway = null;
    }

    private void updateSubscriptions() throws IOException {
        log.debug("Updating Device Subscription");
        deviceInterface.setGateway(gateway);
        final List<GId> currentDeviceIds = gateway.getCurrentDeviceIds();
        if (currentDeviceIds != null) {
            for (final GId gId : currentDeviceIds) {
                final Optional<Device> deviceOptional = deviceRepository.get(gId);
                if (deviceOptional.isPresent()) {
                    final Device device = deviceOptional.get();
                    if (device.getDeviceType() != null) {
                        final Optional<DeviceType> deviceTypeOptional = deviceTypeRepository.get(device.getDeviceType());
                        if (deviceTypeOptional.isPresent()) {
                            log.debug("Adding details to devicePollingData ");
                            devicePollingData.put(device, deviceTypeOptional.get());
                        }
                    }
                }
            }
            if (gateway.getPollingRateInSeconds() <= 0) {
                log.debug("Device polling will be scheduled once as no polling rate found");
                pollDevices();
            }
            subscribe();
        }
    }

    private void pollDevices() throws IOException {
        for (Device device : devicePollingData.keySet()) {
            pollDevice(gateway, device);
        }
    }

    private void pollDevice(final Gateway gateway, final Device device) throws IOException {
        for (final Register register : mapIpAddressToRegister.get(device.getIpAddress())) {
            if (register.getMeasurementMapping() != null) {
                pollingService.initiatePolling(register.getOid(), device, new PduListener() {
                    @Override
                    public void onPduReceived(PDU pdu) {
                        eventPublisher.publishEvent(new ClientDataChangedEvent(gateway, device, register,
                                new DateTime(), pdu.getVariableBindings().get(0).getVariable(), true));
                    }
                });
            }
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
                    public void onPduReceived(PDU pdu) {
                        eventPublisher.publishEvent(new ClientDataChangedEvent(gateway, device, register,
                                new DateTime(), pdu.getType(), false));
                    }
                });
            }
            mapIPAddressToOid.put(device.getIpAddress(), mapOidToPduListener);
            mapIpAddressToRegister.put(device.getIpAddress(), deviceType.getRegisters());
            deviceInterface.subscribe(mapIPAddressToOid);
            devicePollingData.put(device, deviceType);
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            eventPublisher.publishEvent(new GatewayConfigErrorEvent(gateway, new ConfigEventType(ex.getMessage())));
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
        deviceInterface.unsubscribe(device.getIpAddress());
        mapIPAddressToOid.remove(device.getIpAddress());
        mapIpAddressToRegister.remove(device.getIpAddress());
        devicePollingData.remove(device);
    }

    private void refreshScheduler() {
        log.debug("Scheduling Device Polling on user defined interval if polling rate exists");
        if (gateway != null && (gateway.getPollingRateInSeconds() > 0)
                && (currentPollingRateInSeconds.get() != gateway.getPollingRateInSeconds())) {
            terminateTaskIfRunning();
            currentPollingRateInSeconds.set(gateway.getPollingRateInSeconds());
            future = scheduler.scheduleWithFixedDelay(new Runnable() {
                public void run() {
                    try {
                        log.debug("Running scheduled device polling");
                        pollDevices();
                    } catch (IOException e) {
                        log.error("Exception during SNMP Device Polling ", e);
                    }
                }
            }, 1000 * currentPollingRateInSeconds.get());
        }
    }

    private void terminateTaskIfRunning() {
        log.debug("Deleting Task if exists");
        if (future != null && (!future.isCancelled())) {
            future.cancel(true);
        }
    }
}