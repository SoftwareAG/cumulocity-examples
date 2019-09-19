package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import com.cumulocity.agent.snmp.platform.pubsub.service.PubSub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collection;

/**
 * Subscriber to handle messages which subscribes/unsubscribes itself as
 * part of the PostConstruct and PreDestroy spring callbacks
 *
 * @param <PS> PubSub service to subscribe to.
 */
@Slf4j
public abstract class Subscriber<PS extends PubSub> {

    @Autowired
    private PS pubSub;


    public boolean isBatchingSupported() {
        return false;
    }

    public int getBatchSize() {
        return 0;
    }

    public abstract void handleMessage(String message);

    public void handleBulkMessages(Collection<String> messageCollection) {
        throw new UnsupportedOperationException();
    }

    @PostConstruct
    private void subscribe() {
        pubSub.subscribe(this);
    }

    @PreDestroy
    private void unsubscribe() {
        pubSub.unsubscribe(this);
    }
}