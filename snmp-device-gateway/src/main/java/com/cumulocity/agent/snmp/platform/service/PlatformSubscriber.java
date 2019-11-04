package com.cumulocity.agent.snmp.platform.service;

import javax.annotation.PreDestroy;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.cumulocity.agent.snmp.bootstrap.model.BootstrapReadyEvent;
import com.cumulocity.agent.snmp.platform.model.GatewayDataRefreshedEvent;
import com.cumulocity.agent.snmp.platform.model.GatewayManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.ReceivedOperationForGatewayEvent;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.cep.notification.InventoryRealtimeDeleteAwareNotificationsSubscriber;
import com.cumulocity.sdk.client.cep.notification.ManagedObjectDeleteAwareNotification;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.notification.Subscriber;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PlatformSubscriber {

	@Autowired
	private PlatformProvider platformProvider;

	@Autowired
	private DeviceControlApi deviceControlApi;

	@Autowired
	private GatewayDataProvider gatewayDataProvider;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	private InventoryRealtimeDeleteAwareNotificationsSubscriber gatewayNotificationSubscriber;

	private Subscriber<GId, OperationRepresentation> subscriberForOperationsOnGateway;

	@EventListener({ BootstrapReadyEvent.class, GatewayDataRefreshedEvent.class })
	public void subscribe() {
		subscribeGatewayInventoryNotification();
		subscribeGatewayDeviceOperation();
	}

	private void subscribeGatewayInventoryNotification() {
		if (gatewayNotificationSubscriber != null) {
			return;
		}

		GatewayManagedObjectWrapper gatewayDevice = gatewayDataProvider.getGatewayDevice();
		PlatformParameters platformParameters = (PlatformParameters) platformProvider.getPlatform();

		try {
			gatewayNotificationSubscriber = new InventoryRealtimeDeleteAwareNotificationsSubscriber(platformParameters);
			gatewayNotificationSubscriber.subscribe(gatewayDevice.getId().getValue(),
					new SubscriptionListener<String, ManagedObjectDeleteAwareNotification>() {
						@Override
						public void onNotification(Subscription<String> subscription,
								ManagedObjectDeleteAwareNotification notification) {
							// Nothing to do here.
							// The gateway object refresh will take care of updating gateway device
						}

						@Override
						public void onError(Subscription<String> subscription, Throwable throwable) {
							if (throwable instanceof SDKException) {
								SDKException sdkException = (SDKException) throwable;
								if (sdkException.getHttpStatus() == HttpStatus.SC_UNAUTHORIZED) {
									log.error("Device with name '{}' and id '{}' is deleted. "
											+ "Restart the agent and register a new device. "
											+ "\nShutting down agent...", gatewayDevice.getName(), 
											gatewayDevice.getId().getValue());
									System.exit(0);
								}
							}
						}
					});
		} catch (SDKException ex) {
			gatewayNotificationSubscriber = null;

			// Ignore this exception and continue as the subscription will be retried when
			// the Gateway data is refreshed next time.
			log.warn("Couldn't enable the subscription for inventory changes for the gateway device with name '{}' and id '{}'. "
					+ "This subscription will be retried later.", gatewayDevice.getName(), gatewayDevice.getId().getValue());
			log.debug(ex.getMessage(), ex);
		}
	}

	private void subscribeGatewayDeviceOperation() {
		if (subscriberForOperationsOnGateway != null) {
			return;
		}

		GatewayManagedObjectWrapper gatewayDevice = gatewayDataProvider.getGatewayDevice();

		try {
			subscriberForOperationsOnGateway = deviceControlApi.getNotificationsSubscriber();
			subscriberForOperationsOnGateway.subscribe(gatewayDevice.getId(),
					new SubscriptionListener<GId, OperationRepresentation>() {
						@Override
						public void onNotification(Subscription<GId> subscription, OperationRepresentation operation) {

							if (gatewayDevice.getId().equals(subscription.getObject())) {
								log.debug("Device '{}', with id '{}', received notification.", gatewayDevice.getName(),
										gatewayDevice.getId().getValue(), subscription.getObject().getValue());

								eventPublisher.publishEvent(new ReceivedOperationForGatewayEvent(gatewayDevice.getId(),
										gatewayDevice.getName(), operation));
							} else {
								log.debug(
										"Device '{}', with id '{}', received a notification which is meant for device with id '{}'.",
										gatewayDevice.getName(), gatewayDevice.getId().getValue(),
										subscription.getObject().getValue());
							}
						}

						@Override
						public void onError(Subscription<GId> subscription, Throwable throwable) {
							log.debug("Error occurred while listening to operations for the device with name '{}' and id '{}'.",
									gatewayDevice.getName(), gatewayDevice.getId().getValue(), throwable);
						}
					});

			log.info("Enabled the subscription for listening to operations for the gateway device with name '{}' and id '{}'.",
					gatewayDevice.getName(), gatewayDevice.getId().getValue());
		} catch (Throwable t) {
			subscriberForOperationsOnGateway = null;

			// Ignore this exception and continue as the subscription will be retried when
			// the Gateway data is refreshed next time.
			log.warn("Couldn't enable the subscription for listening to operations for the gateway device with name '{}' and id '{}'. "
					+ "This subscription will be retried later.", gatewayDevice.getName(), gatewayDevice.getId().getValue());
			log.debug(t.getMessage(), t);
		}
	}

	@PreDestroy
	public void unsubscribe() {
		if (gatewayNotificationSubscriber != null) {
			gatewayNotificationSubscriber.disconnect();
		}

		if (subscriberForOperationsOnGateway != null) {
			subscriberForOperationsOnGateway.disconnect();
		}
	}
}
