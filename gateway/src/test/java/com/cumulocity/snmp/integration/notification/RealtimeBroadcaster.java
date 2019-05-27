package com.cumulocity.snmp.integration.notification;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.integration.platform.subscription.OperationNotification;

public interface RealtimeBroadcaster {

    void sendDelete(final String deviceId);

    void sendUpdate(final ManagedObjectRepresentation representation);

    void sendOperation(String deviceId, OperationNotification operation);
}
