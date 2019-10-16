package com.cumulocity.agent.snmp.platform.pubsub.service.subscription;

import com.cumulocity.agent.snmp.persistence.Queue;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.Subscriber;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
public class SingleMessageSubscription extends Subscription {

    public SingleMessageSubscription(Queue queue, Subscriber<?> subscriber) {
        super(queue, subscriber);
    }

    @Override
    protected boolean deliver() {
        boolean continueDelivering = false;

        String messageFromQueue = getQueue().dequeue();
        if (messageFromQueue != null && !messageFromQueue.isEmpty()) {
            try {
                getSubscriber().onMessage(messageFromQueue);

                continueDelivering = true;
            } catch(Throwable t) {
                // Throwable is caught to ensure that the message is not lost on any condition
                log.error("Failed to deliver the message from the '{}' Queue to the '{}' Subscriber. Message is put back in the queue for retry.", getQueue().getName(), getSubscriber().getClass().getSimpleName(), t);
                rollbackMessagesToQueue(Collections.singletonList(messageFromQueue));
            }
        }

        return continueDelivering;
    }
}
