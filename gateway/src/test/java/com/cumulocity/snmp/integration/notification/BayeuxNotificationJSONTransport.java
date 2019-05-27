package com.cumulocity.snmp.integration.notification;

import org.cometd.server.BayeuxServerImpl;
import org.cometd.server.transport.JSONTransport;

import javax.servlet.http.HttpServletRequest;

public class BayeuxNotificationJSONTransport extends JSONTransport {

    public BayeuxNotificationJSONTransport(BayeuxServerImpl bayeux) {
        super(bayeux);
    }

    @Override
    public void init() {
        super.init();
        setOption(MAX_QUEUE_OPTION, 100);
    }

    @Override
    public boolean accept(HttpServletRequest request) {
        return "POST".equals(request.getMethod()) && request.getHeader("X-Id") == null;
    }

}
