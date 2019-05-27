package com.cumulocity.snmp.integration.notification;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.integration.platform.subscription.OperationNotification;
import lombok.RequiredArgsConstructor;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.bayeux.server.LocalSession;
import org.cometd.server.BayeuxServerImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

import static org.cometd.bayeux.Message.DATA_FIELD;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BayeuxRealtimeBroadcaster implements RealtimeBroadcaster {

    private static final String REALTIME_ACTION_FIELD = "realtimeAction";

    private static final String REALTIME_UPDATE = "UPDATE";
    private static final String REALTIME_CREATE = "CREATE";
    private static final String REALTIME_DELETE = "DELETE";

    private final BayeuxServerImpl server;

    @Override
    public void sendOperation(final String deviceId, final OperationNotification operation) {
        publishMessage("/operations/" + deviceId, operation);
    }

    private void publishMessage(final String channel, final Object message) {
        executeWithSession(new SessionHandler() {
            @Override
            public void apply(LocalSession session) {
                final ClientSessionChannel clientSessionChannel = session.getChannel(channel);
                clientSessionChannel.publish(message);
            }
        });
    }

    @Override
    public void sendDelete(final String deviceId) {
        sendRealtime("/managedobjects/" + deviceId, deviceId, REALTIME_DELETE);
    }

    @Override
    public void sendUpdate(final ManagedObjectRepresentation representation) {
        sendRealtime("/managedobjects/" + representation.getId().getValue(), representation, REALTIME_UPDATE);
    }

    private void executeWithSession(SessionHandler sessionHandler) {
        final LocalSession session = openSession();
        try {
            sessionHandler.apply(session);
            closeSession(session);
        } catch (final Exception ex) {
            closeSession(session);
        }
    }

    private void sendRealtime(final String channelName, final Object message, final String action) {
        executeWithSession(new SessionHandler() {
            @Override
            public void apply(LocalSession session) {
                final ClientSessionChannel channel = session.getChannel(channelName);
                HashMap<String, Object> messageMap = new HashMap<>();
                messageMap.put(DATA_FIELD, message);
                messageMap.put(REALTIME_ACTION_FIELD, action);
                channel.publish(messageMap);
            }
        });
    }

    private LocalSession openSession() {
        final LocalSession newLocalSession = server.newLocalSession("notifications-service-session");
        newLocalSession.handshake();
        return newLocalSession;
    }

    private void closeSession(Object obj) {
        LocalSession session = (LocalSession) obj;
        session.disconnect();
    }

    private interface SessionHandler {
        void apply(LocalSession session);
    }

}
