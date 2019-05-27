package com.cumulocity.snmp.integration.notification.controller;

import org.cometd.server.BayeuxServerImpl;

public class CepRealtimeController extends NotificationController {
    public CepRealtimeController(BayeuxServerImpl server) {
        super(server);
    }
}
