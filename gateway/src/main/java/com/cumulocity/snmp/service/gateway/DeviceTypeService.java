package com.cumulocity.snmp.service.gateway;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.model.device.DeviceAddedEvent;
import com.cumulocity.snmp.model.gateway.DeviceTypeAddedEvent;
import com.cumulocity.snmp.model.gateway.DeviceTypeUpdatedEvent;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.notification.Notifications;
import com.cumulocity.snmp.model.notification.platform.ManagedObjectListener;
import com.cumulocity.snmp.model.notification.platform.Subscriptions;
import com.cumulocity.snmp.model.type.DeviceType;
import com.cumulocity.snmp.repository.DeviceTypeInventoryRepository;
import com.cumulocity.snmp.repository.core.Repository;
import com.cumulocity.snmp.repository.platform.PlatformProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeviceTypeService {

    private final Repository<DeviceType> deviceTypePeristedRepository;
    private final DeviceTypeInventoryRepository deviceTypeInventoryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PlatformProvider platformProvider;
    private final Notifications notifications;
    private final Subscriptions subscribers = new Subscriptions();
    private final ObjectMapper objectMapper;

    @EventListener
    @RunWithinContext
    public void addDeviceType(final DeviceAddedEvent event) throws ExecutionException {
        final Gateway gateway = event.getGateway();
        final Device device = event.getDevice();
        if(device.getDeviceType()!=null) {
            final Optional<DeviceType> newDeviceTypeOptional = deviceTypeInventoryRepository.get(gateway, device.getDeviceType());
            if (newDeviceTypeOptional.isPresent()) {
                final Optional<DeviceType> previousDeviceTypeOptional = deviceTypePeristedRepository.get(newDeviceTypeOptional.get().getId());
                if (!previousDeviceTypeOptional.equals(newDeviceTypeOptional)) {
                    final DeviceType deviceType = deviceTypePeristedRepository.save(newDeviceTypeOptional.get());
                    eventPublisher.publishEvent(new DeviceTypeAddedEvent(event.getGateway(), device, deviceType));
                    unsubscribe(deviceType);
                    subscribe(gateway, device, deviceType);
                }
            }
        }
    }

    private void subscribe(final Gateway gateway, final Device device, final DeviceType deviceType) throws ExecutionException {
        final PlatformParameters platform = platformProvider.getPlatformProperties(gateway);
        final GId id = deviceType.getId();

        subscribers.add(id, notifications.subscribeInventory(platform, id, new ManagedObjectListener() {
            public void onUpdate(Object value) {
                DeviceType newDeviceType = objectMapper.convertValue(value, DeviceType.class);
                eventPublisher.publishEvent(new DeviceTypeUpdatedEvent(gateway, device, newDeviceType));
            }

            @Override
            public void onDelete() {
                subscribers.disconnect(id);
            }
        }));
    }

    private void unsubscribe(DeviceType deviceType) {
        subscribers.disconnect(deviceType.getId());
    }
}
