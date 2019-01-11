package com.cumulocity.snmp.service.gateway;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.configuration.service.GatewayConfigurationProperties;
import com.cumulocity.snmp.factory.gateway.GatewayFactory;
import com.cumulocity.snmp.factory.platform.IdentityFactory;
import com.cumulocity.snmp.factory.platform.ManagedObjectFactory;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.GatewayAddedEvent;
import com.cumulocity.snmp.model.gateway.GatewayRemovedEvent;
import com.cumulocity.snmp.repository.DeviceCredentialsRepository;
import com.cumulocity.snmp.repository.IdentityRepository;
import com.cumulocity.snmp.repository.ManagedObjectRepository;
import com.cumulocity.snmp.repository.core.Repository;
import com.cumulocity.snmp.utils.gateway.Scheduler;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class BootstrapService {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    DeviceCredentialsRepository deviceCredentialsRepository;

    @Autowired
    Repository<Gateway> gatewayRepository;

    @Autowired
    GatewayFactory gatewayFactory;

    @Autowired
    IdentityRepository identityRepository;

    @Autowired
    IdentityFactory identityFactory;

    @Autowired
    ManagedObjectRepository inventoryRepository;

    @Autowired
    ManagedObjectFactory managedObjectFactory;

    @Autowired
    GatewayConfigurationProperties properties;

    @Autowired
    Scheduler scheduler;

    private List<GId> initialized = new ArrayList<>();

    @PostConstruct
    public void init() {
        scheduler.scheduleOnce(new Runnable() {
            public void run() {
                scheduler.scheduleWithFixedDelay(new Runnable() {
                    public void run() {
                        try {
                            syncGateways();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Async
    public synchronized void syncGateways() throws InvocationTargetException, IllegalAccessException {
        refreshGateways();
        createGateway();
    }

    private void createGateway() throws InvocationTargetException, IllegalAccessException {
        final Collection<Gateway> all = getAllGateways();
        if (all.isEmpty()) {
            final String identifier = properties.getIdentifier();
            final Optional<DeviceCredentialsRepresentation> gatewayCredentialsOptional = deviceCredentialsRepository.get(identifier);
            if (gatewayCredentialsOptional.isPresent()) {
                final DeviceCredentialsRepresentation credentials = gatewayCredentialsOptional.get();

                final Optional<ExternalIDRepresentation> existingExternalIdOptional = identityRepository.get(credentials, identityFactory.createID(properties.getIdentifier()));
                if (existingExternalIdOptional.isPresent()) {
                    final Optional<ManagedObjectRepresentation> managedObjectOptional = inventoryRepository.get(credentials, existingExternalIdOptional.get().getManagedObject().getId());
                    create(credentials, managedObjectOptional, existingExternalIdOptional);
                } else {
                    final Optional<ManagedObjectRepresentation> managedObjectOptional = inventoryRepository.save(credentials, managedObjectFactory.create(identifier));
                    create(credentials, managedObjectOptional, existingExternalIdOptional);
                }
            }
        }
    }

    private void create(DeviceCredentialsRepresentation credentials, Optional<ManagedObjectRepresentation> managedObjectOptional, Optional<ExternalIDRepresentation> existingExternalIdOptional) throws InvocationTargetException, IllegalAccessException {
        if (managedObjectOptional.isPresent()) {
            final ManagedObjectRepresentation managedObject = managedObjectOptional.get();
            final Optional<Gateway> newGatewayOptional = gatewayFactory.create(credentials, managedObject);
            if (newGatewayOptional.isPresent()) {
                final Gateway gateway = newGatewayOptional.get();
                final ExternalIDRepresentation externalId = identityFactory.create(properties.getIdentifier(), managedObject);
                if (!existingExternalIdOptional.isPresent()) {
                    identityRepository.save(gateway, externalId);
                }
                gatewayRepository.save(gateway);
                eventPublisher.publishEvent(new GatewayAddedEvent(gateway));
                initialized.add(gateway.getId());
            }
        }
    }

    private void refreshGateways() throws InvocationTargetException, IllegalAccessException {
        for (final Gateway gateway : getAllGateways()) {
            final Optional<ManagedObjectRepresentation> managedObjectOptional = inventoryRepository.get(gateway);
            if (managedObjectOptional.isPresent()) {
                final ManagedObjectRepresentation managedObject = managedObjectOptional.get();
                final Optional<Gateway> newGatewayOptional = gatewayFactory.create(gateway, managedObject);
                if (newGatewayOptional.isPresent()) {
                    final Gateway newGateway = newGatewayOptional.get();
                    if (!gateway.equals(newGateway)) {
                        gatewayRepository.save(newGateway);
                    }
                    if (!initialized.contains(newGateway.getId())) {
                        eventPublisher.publishEvent(new GatewayAddedEvent(newGateway));
                        initialized.add(newGateway.getId());
                    }

                    final Optional<ExternalIDRepresentation> externalIdOptional = identityRepository.get(newGateway, identityFactory.createID(properties.getIdentifier()));
                    if (!externalIdOptional.isPresent()) {
                        removeGateway(newGateway);
                    }
                } else {
                    removeGateway(gateway);
                }
            } else {
                removeGateway(gateway);
            }
        }
    }

    private Collection<Gateway> getAllGateways() {
        return FluentIterable.from(gatewayRepository.findAll()).filter(new Predicate<Gateway>() {
            public boolean apply(Gateway gateway) {
                return ("device_" + properties.getIdentifier()).equalsIgnoreCase(gateway.getName());
            }
        }).toList();
    }

    private void removeGateway(Gateway gateway) {
        initialized.remove(gateway.getId());
        final int numberOfRetries = gateway.increaseNumberOfRetries();
        log.warn("Gateway is not responding {}/{} ({})", gateway.getTenant(), gateway.getName(), numberOfRetries);
        if (numberOfRetries < 10) {
            gatewayRepository.save(gateway);
        } else {
            log.warn("Removing  gateway {}/{}", gateway.getTenant(), gateway.getName());
            gatewayRepository.delete(gateway.getId());
            eventPublisher.publishEvent(new GatewayRemovedEvent(gateway));
        }
    }

}
