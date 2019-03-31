package com.cumulocity.snmp.service.client;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.model.core.ConfigEventType;
import com.cumulocity.snmp.model.device.DeviceAddedEvent;
import com.cumulocity.snmp.model.device.DeviceRemovedEvent;
import com.cumulocity.snmp.model.gateway.*;
import com.cumulocity.snmp.model.gateway.client.ClientDataChangedEvent;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.gateway.type.core.Register;
import com.cumulocity.snmp.model.type.DeviceType;
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

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    Scheduler scheduler;

    Gateway gateway;
    AtomicInteger counter = new AtomicInteger(1);

    Map<String, Map<String, PduListener>> mapIPAddressToOid = new ConcurrentHashMap<>();
    Map<String, List<Register>> mapIpAddressToRegister = new ConcurrentHashMap<>();
    Map<Device, DeviceType> devicePollingData = new ConcurrentHashMap<>();

    @PostConstruct
    public void scheduleDevicePolling() {
        scheduler.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                if (gateway != null
                        && gateway.getPollingRateInSeconds() > 0
                        && (counter.get() >= gateway.getPollingRateInSeconds())) {
                    try {
                        log.debug("Running scheduled device polling");
                        pollDevices();
                        counter.getAndSet(0);
                    } catch (IOException e) {
                        log.error("Exception during SNMP Device Polling ", e);
                    }
                }
                counter.incrementAndGet();
            }
        }, 1000);
    }

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscription(final DeviceTypeAddedEvent event) {
        final Device device = event.getDevice();
        this.gateway = event.getGateway();
        subscribe(device, event.getDeviceType());
    }

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscription(final DeviceTypeUpdatedEvent event) {
        final Device device = event.getDevice();
        this.gateway = event.getGateway();
        subscribe(device, event.getDeviceType());
    }

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscription(final DeviceAddedEvent event) {
        final Gateway gateway = event.getGateway();
        this.gateway = event.getGateway();
        final Optional<DeviceType> deviceTypeOptional = deviceTypeRepository.get(event.getDevice().getDeviceType());
        if (deviceTypeOptional.isPresent()) {
            final Device device = event.getDevice();
            subscribe(device, deviceTypeOptional.get());
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscriptions(final GatewayAddedEvent event) {
        this.gateway = event.getGateway();
        try {
            updateSubscriptions();
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            eventPublisher.publishEvent(new GatewayConfigErrorEvent(gateway, new ConfigEventType(ex.getMessage())));
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscriptions(final GatewayUpdateEvent event) {
        try {
            this.gateway = event.getGateway();
            updateSubscriptions();
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void unsubscribe(final DeviceRemovedEvent event) {
        unsubscribe(event.getDevice());
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
                    final Optional<DeviceType> deviceTypeOptional = deviceTypeRepository.get(device.getDeviceType());
                    if (deviceTypeOptional.isPresent()) {
                        log.debug("Adding details to devicePollingData ", device.getIpAddress());
                        devicePollingData.put(device, deviceTypeOptional.get());
                    }
                }
            }
            if (gateway.getPollingRateInSeconds() <= 0) {
                log.debug("No Gateway polling rate found");
                pollDevices();
            }
            subscribe();
        }
    }

    private void pollDevices() throws IOException {
        log.debug("Device Polling Data size :: " + devicePollingData.size());
        for (Device device : devicePollingData.keySet()) {
            pollDevice(gateway, device);
        }
    }

    private void pollDevice(final Gateway gateway, final Device device) throws IOException {
        for (Map.Entry<String, List<Register>> ipAddressToRegister : mapIpAddressToRegister.entrySet()) {
            for (final Register register : ipAddressToRegister.getValue()) {
                if (register.getMeasurementMapping() != null) {
                    log.debug("Measurement mapping OID : " + register.getOid());
                    deviceInterface.initiatePolling(register.getOid(), ipAddressToRegister.getKey(), new PduListener() {
                        @Override
                        public void onPduReceived(PDU pdu) {
                            eventPublisher.publishEvent(new ClientDataChangedEvent(gateway, device, register,
                                    new DateTime(), pdu.getVariableBindings().get(0).getVariable(), true));
                        }
                    });
                }
            }
        }
    }

    private void subscribe() {
        for (Map.Entry<Device, DeviceType> deviceData : devicePollingData.entrySet()) {
            subscribe(deviceData.getKey(), deviceData.getValue());
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
        log.debug("Unsubscribed device");
        deviceInterface.unsubscribe(device.getIpAddress());
        devicePollingData.remove(device);
    }
}