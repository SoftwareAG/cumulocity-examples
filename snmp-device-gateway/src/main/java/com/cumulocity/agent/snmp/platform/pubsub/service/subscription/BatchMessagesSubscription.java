package com.cumulocity.agent.snmp.platform.pubsub.service.subscription;

import com.cumulocity.agent.snmp.persistence.Queue;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.Subscriber;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public class BatchMessagesSubscription extends Subscription {

    public BatchMessagesSubscription(Queue queue, Subscriber subscriber) {
        super(queue, subscriber);
    }

    @Override
    protected boolean deliver() {
        boolean continueDelivering = false;

        int batchSize = getSubscriber().getBatchSize();

        Collection<String> messagesFromQueue = new ArrayList<>(batchSize);
        int size = getQueue().drainTo(messagesFromQueue, batchSize);

        if(size > 0) {
            try {
                getSubscriber().onMessages(messagesFromQueue);

                if(size >= batchSize) {
                    continueDelivering = true;
                }
            } catch(Throwable t) {
                // Throwable is caught to ensure that the messages are not lost on any condition
                log.error("Failed to deliver the messages from '{}' Queue to the '{}' Subscriber. Messages are put back in the queue for retry.", getQueue().getName(), getSubscriber().getClass().getSimpleName(), t);
                rollbackMessagesToQueue(messagesFromQueue);
            }
        }

        return continueDelivering;
    }
}
