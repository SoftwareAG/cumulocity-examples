package com.cumulocity.agent.snmp.bootstrap.service;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import c8y.Hardware;
import c8y.IsDevice;
import c8y.Mobile;
import c8y.RequiredAvailability;
import c8y.SupportedOperations;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import com.cumulocity.agent.snmp.bootstrap.model.BootstrapReadyEvent;
import com.cumulocity.agent.snmp.bootstrap.model.CredentialsAvailableEvent;
import com.cumulocity.agent.snmp.bootstrap.model.DeviceCredentials;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.config.PlatformProvider;
import com.cumulocity.agent.snmp.platform.model.PlatformConnectionReadyEvent;
import com.cumulocity.agent.snmp.repository.DataStore;
import com.cumulocity.agent.snmp.utils.Constants;
import com.cumulocity.model.Agent;
import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BootstrapService implements InitializingBean {

	private final TaskScheduler scheduler;

	private final IdentityApi identityApi;

	private final InventoryApi inventoryApi;

	private final DataStore credentialStore;

	private final GatewayProperties properties;

	private final PlatformProvider platformProvider;

	private final ApplicationEventPublisher eventPublisher;

	@Override
	public void afterPropertiesSet() {
		scheduleDeviceCredentialsPoll();
	}

	@EventListener
	public void createDeviceIfNotExist(PlatformConnectionReadyEvent platformConnectionReadyEvent) {
		log.info("Platform connection is ready, creating device if it's not there");

		try {
			ManagedObjectRepresentation deviceMO;

			ID deviceId = createID(properties.getGatewayIdentifier());
			Optional<ExternalIDRepresentation> existingIdentity = getExternalID(deviceId);

			if (!existingIdentity.isPresent()) {
				deviceMO = createGatewayManagedObject();
				createExternalID(deviceId, deviceMO);
				log.info("Device created with ID: {}", deviceMO.getId().getValue());
			} else {
				GId deviceMoID = existingIdentity.get().getManagedObject().getId();
				deviceMO = inventoryApi.get(deviceMoID);
				log.info("Device with ID: {} already present in the platform", deviceMO.getId().getValue());

				if (!platformConnectionReadyEvent.getCurrentUser().equals(deviceMO.getOwner())) {
					log.error("Current gateway device managed object is owned by another user ({}) , please change it to [{}]. "
						+ "\n Shutting down platform...", deviceMO.getOwner(), platformConnectionReadyEvent.getCurrentUser());
					System.exit(0);
				}
			}

			eventPublisher.publishEvent(new BootstrapReadyEvent(deviceMO));

		} catch (BeanCreationException e) {
			if (detectInvalidCredentials(e)) {
				log.error("Invalid device credentials detected! Removing local cached credentials...");
				credentialStore.remove();
				log.info("Local credentials removed! Please bootstrap the device agent again. \n Shutting down platform...");
				System.exit(0);
			}

			throw e;
		}
	}

	private boolean detectInvalidCredentials(BeanCreationException e) {
		Throwable cause = e;

		while (!Objects.isNull(cause.getCause())) {
			if (cause.getCause() instanceof SDKException) {
				SDKException sdkException = (SDKException) cause.getCause();
				if (sdkException.getHttpStatus() == HttpStatus.SC_UNAUTHORIZED) {
					return true;
				}
			}

			cause = cause.getCause();
		}

		return false;
	}

	private void scheduleDeviceCredentialsPoll() {
		scheduler.scheduleWithFixedDelay(() -> {
			if (platformProvider.isCredentialsAvailable()) {
				return;
			}

			log.info("Polling device credentials...");

			DeviceCredentials deviceCredentials;
			Optional<DeviceCredentials> credentialsOptional = credentialStore.get();

			if (credentialsOptional.isPresent() && !properties.isForcedBootstrap()) {
				log.info("Credentials available locally");
				deviceCredentials = credentialsOptional.get();
			} else {
				log.info("Credentials not available locally or bootstrap is forced, polling from server...");
				deviceCredentials = pollDeviceCredentials();
			}

			if (!Objects.isNull(deviceCredentials)) {
				log.info("Obtained device credentials");
				eventPublisher.publishEvent(new CredentialsAvailableEvent(deviceCredentials));
			}
		}, properties.getBootstrapProperties().getBootstrapDelay());
	}

	private DeviceCredentials pollDeviceCredentials() {
		DeviceCredentials deviceCredentials = null;
		DeviceCredentialsRepresentation credRep;

		try {
			credRep = platformProvider.getBootstrapPlatform().getDeviceCredentialsApi().pollCredentials(properties.getGatewayIdentifier());
			deviceCredentials = new DeviceCredentials(credRep.getTenantId(), credRep.getUsername(), credRep.getPassword());
			credentialStore.store(deviceCredentials);
		} catch (SDKException e) {
			if (e.getHttpStatus() == HttpStatus.SC_NOT_FOUND) {
				log.warn("There is no newDeviceRequest or deviceRequest has not been accepted for device id {}. "
						+ "Please register device manually under Device Management user interface", properties.getGatewayIdentifier());
			} else {
				throw e;
			}
		}

		return deviceCredentials;
	}

	private ID createID(String identifier) {
		return new ID(Constants.EXTERNAL_ID_TYPE, identifier);
	}

	private Optional<ExternalIDRepresentation> getExternalID(ID id) {
		try {
			ExternalIDRepresentation existingExternalId = identityApi.getExternalId(id);
			return ofNullable(existingExternalId);
		} catch (SDKException ex) {
			if (ex.getHttpStatus() == HttpStatus.SC_NOT_FOUND) {
				return empty();
			}

			throw ex;
		}
	}

	private void createExternalID(ID id, ManagedObjectRepresentation deviceMO) {
		ExternalIDRepresentation externalId = new ExternalIDRepresentation();
		externalId.setExternalId(id.getValue());
		externalId.setType(id.getType());
		externalId.setManagedObject(deviceMO);

		identityApi.create(externalId);
	}

	private ManagedObjectRepresentation createGatewayManagedObject() {
		SupportedOperations operation = new SupportedOperations();
		operation.add(Constants.SUPPORTED_OPERATIONS);

		ManagedObjectRepresentation deviceMO = new ManagedObjectRepresentation();
		deviceMO.setName(properties.getGatewayIdentifier());
		deviceMO.setType(Constants.GATEWAY_TYPE);
		deviceMO.set(new Agent());
		deviceMO.set(new IsDevice());
		deviceMO.set(new Hardware());
		deviceMO.set(new Mobile());
		deviceMO.set(new Object(), Constants.C8Y_SNMP_GATEWAY);
		deviceMO.set(new RequiredAvailability(properties.getGatewayAvailabilityInterval()));
		deviceMO.set(operation);

		return inventoryApi.create(deviceMO);
	}
}