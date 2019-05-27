package com.cumulocity.snmp.integration.notification;

import com.cumulocity.common.notification.BaseSvensonJSONContext;
import com.cumulocity.model.JSONBase;
import org.cometd.bayeux.server.ServerMessage.Mutable;
import org.cometd.common.JSONContext;
import org.cometd.server.ServerMessageImpl;

import java.util.Map;

public class ServerSvensonJSONContext extends BaseSvensonJSONContext<Mutable> implements JSONContext.Server {

    public ServerSvensonJSONContext() {
        super(JSONBase.getJSONGenerator(), JSONBase.getJSONParser());
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected Class targetClass() {
        return ServerMessageImpl.class;
    }

    @Override
    protected Mutable toMessage(Map<String, Object> messageProperties) {
        final ServerMessageImpl message = new ServerMessageImpl();
        message.putAll(messageProperties);
        return message;
    }
}
