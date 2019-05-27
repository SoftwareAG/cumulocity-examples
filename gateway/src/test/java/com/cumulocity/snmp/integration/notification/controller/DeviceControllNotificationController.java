package com.cumulocity.snmp.integration.notification.controller;

import org.cometd.server.BayeuxServerImpl;

public class DeviceControllNotificationController extends NotificationController {
    public DeviceControllNotificationController(BayeuxServerImpl server) {
        super(server);
    }
}
