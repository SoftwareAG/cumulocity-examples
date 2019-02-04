package com.cumulocity.snmp.model.notification.platform;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.sdk.client.notification.Subscriber;
import lombok.Synchronized;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Subscriptions {
    private final Map<GId, Subscriber> subscribers = new ConcurrentHashMap<>();

    @Synchronized
    public void add(GId id, Subscriber subscriber) {
        subscribers.put(id, subscriber);
    }

    @Synchronized
    public boolean disconnect(GId id) {
        if (subscribers.containsKey(id)) {
            final Subscriber remove = subscribers.remove(id);
            remove.disconnect();
            return true;
        }
        return false;
    }
}
