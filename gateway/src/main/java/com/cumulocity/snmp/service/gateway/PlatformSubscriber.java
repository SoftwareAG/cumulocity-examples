package com.cumulocity.snmp.service.gateway;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.cep.notification.ManagedObjectDeleteAwareNotification;
import com.cumulocity.sdk.client.devicecontrol.notification.OperationNotificationSubscriber;
import com.cumulocity.sdk.client.notification.Subscriber;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.factory.gateway.GatewayFactory;
import com.cumulocity.snmp.model.device.DeviceAddedEvent;
import com.cumulocity.snmp.model.device.DeviceRemovedEvent;
import com.cumulocity.snmp.model.device.DeviceUpdatedEvent;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.GatewayAddedEvent;
import com.cumulocity.snmp.model.gateway.GatewayRemovedEvent;
import com.cumulocity.snmp.model.gateway.GatewayUpdateEvent;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.notification.Notifications;
import com.cumulocity.snmp.model.notification.platform.ManagedObjectListener;
import com.cumulocity.snmp.model.notification.platform.OperationListener;
import com.cumulocity.snmp.model.notification.platform.Subscriptions;
import com.cumulocity.snmp.model.operation.OperationEvent;
import com.cumulocity.snmp.platform.PlatformSubscribedEvent;
import com.cumulocity.snmp.platform.PlatformUnsubscribedEvent;
import com.cumulocity.snmp.repository.ManagedObjectRepository;
import com.cumulocity.snmp.repository.OperationRepository;
import com.cumulocity.snmp.repository.core.Repository;
import com.cumulocity.snmp.repository.platform.PlatformProvider;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PlatformSubscriber {

    private final Subscriptions managedObjectSubscribers = new Subscriptions();
    private final Subscriptions operationSubscribers = new Subscriptions();
    @Autowired
    private PlatformProvider platformProvider;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private Repository<Gateway> gatewayRepository;
    @Autowired
    private GatewayFactory gatewayFactory;
    @Autowired
    private ManagedObjectRepository managedObjectRepository;
    @Autowired
    private Notifications notifications;
    @Autowired
    private OperationRepository operationRepository;

    @EventListener
    @RunWithinContext
    public synchronized void refreshSubscriptions(final GatewayAddedEvent event) {
        try {
            final Gateway gateway = event.getGateway();
            final PlatformParameters platform = platformProvider.getPlatformProperties(gateway);

            unsubscribeGateway(gateway);
            subscribeGatewayInventory(platform, gateway);
            subscribeGatewayOperations(platform, gateway);
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void subscribe(final DeviceAddedEvent deviceAddedEvent) {
        try {
            final Gateway gateway = deviceAddedEvent.getGateway();
            final Device device = deviceAddedEvent.getDevice();
            final PlatformParameters platform = platformProvider.getPlatformProperties(gateway);

            subscribeDeviceOperations(platform, gateway, device);
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void unsubscribe(final GatewayRemovedEvent event) {
        try {
            unsubscribeGateway(event.getGateway());
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @EventListener
    @RunWithinContext
    public synchronized void unsubscribe(final DeviceRemovedEvent deviceRemovedEvent) {
        try {
            unsubscribeDevice(deviceRemovedEvent.getDevice());
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void subscribeDeviceOperations(PlatformParameters platform, final Gateway gateway, final Device device) {
        final OperationNotificationSubscriber operationsSubscriber = notifications.subscribeOperations(platform, device.getId(), new OperationListener() {
            public void onCreate(OperationRepresentation operationRepresentation) {
                log.debug("operationRepresentation", operationRepresentation);
            }
        });
        operationSubscribers.add(device.getId(), operationsSubscriber);
    }

    private void subscribeGatewayOperations(final PlatformParameters platform, final Gateway gateway) {
        final OperationNotificationSubscriber operationSubscriber = notifications.subscribeOperations(platform, gateway.getId(), new OperationListener() {
            public void onCreate(OperationRepresentation operationRepresentation) {
                Map<String, String> ipRange = (HashMap) operationRepresentation.getAttrs().get("c8y_SnmpAutoDiscovery");
                Map<String, String> snmpDevice = (HashMap) operationRepresentation.getAttrs().get("c8y_SNMPDevice");
                final Optional<ManagedObjectRepresentation> managedObject = managedObjectRepository.get(gateway);
                try {
                    if (managedObject.isPresent()) {
                        final Optional<Gateway> newGatewayOptional = gatewayFactory.create(gateway, managedObject.get());
                        if (newGatewayOptional.isPresent()) {
                            final Gateway newGateway = newGatewayOptional.get();
                            gatewayRepository.save(newGateway);

                            if (ipRange != null && ipRange.get("ipRange") != null) {
                                eventPublisher.publishEvent(new OperationEvent(newGateway, operationRepresentation.getId()));
                            } else if (snmpDevice != null) {
                                String deviceId = snmpDevice.get("id");
                                eventPublisher.publishEvent(new DeviceUpdatedEvent(newGateway, GId.asGId(deviceId)));
                            }
                        }
                    }
                } catch (InvocationTargetException e) {
                    log.error(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage(), e);
                }

            }
        });
        operationSubscribers.add(gateway.getId(), operationSubscriber);
    }

    private void subscribeGatewayInventory(final PlatformParameters platform, final Gateway gateway) {
        final Subscriber<String, ManagedObjectDeleteAwareNotification> inventorySubscriber = notifications.subscribeInventory(platform, gateway.getId(), new ManagedObjectListener() {
            @Override
            public void onUpdate(Object value) throws InvocationTargetException, IllegalAccessException {
                final Optional<ManagedObjectRepresentation> managedObject = managedObjectRepository.get(gateway);
                if (managedObject.isPresent()) {
                    final Optional<Gateway> newGatewayOptional = gatewayFactory.create(gateway, managedObject.get());
                    if (newGatewayOptional.isPresent()) {
                        final Gateway newGateway = newGatewayOptional.get();
                        gatewayRepository.save(newGateway);
                        eventPublisher.publishEvent(new GatewayUpdateEvent(newGateway));
                    }
                }
            }
        });
        managedObjectSubscribers.add(gateway.getId(), inventorySubscriber);
        eventPublisher.publishEvent(new PlatformSubscribedEvent(gateway));
    }

    private void unsubscribeGateway(final Gateway gateway) {
        if (managedObjectSubscribers.disconnect(gateway.getId())) {
            eventPublisher.publishEvent(new PlatformUnsubscribedEvent(gateway));
        }
    }

    private void unsubscribeDevice(final Device device) {
        managedObjectSubscribers.disconnect(device.getId());
    }
}
