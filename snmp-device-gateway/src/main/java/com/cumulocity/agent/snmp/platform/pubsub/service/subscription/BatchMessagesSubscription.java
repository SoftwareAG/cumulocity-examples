package com.cumulocity.agent.snmp.platform.pubsub.service.subscription;

import com.cumulocity.agent.snmp.persistence.Queue;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.PlatformPublishException;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.Subscriber;

import java.util.ArrayList;
import java.util.Collection;

public class BatchMessagesSubscription extends Subscription {

    public BatchMessagesSubscription(Queue queue, Subscriber subscriber) {
        super(queue, subscriber);
    }

    @Override
    protected boolean process() throws PlatformPublishException {
        boolean continueProcessing = true;

        int batchSize = getSubscriber().getBatchSize();

        Collection<String> messagesFromQueue = new ArrayList<>(batchSize);
        int size = getQueue().drainTo(messagesFromQueue, batchSize);

        if(size > 0) {
            getSubscriber().onMessages(messagesFromQueue);
        }

        if(size < batchSize) {
            continueProcessing = false;
        }

        return continueProcessing;
    }
}
