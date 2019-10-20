package com.cumulocity.agent.snmp.platform.pubsub.service.subscription;

import java.util.Collection;

import com.cumulocity.agent.snmp.persistence.Queue;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.Subscriber;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Drains the queue and delivers the messages to the subscriber.
 *
 * Before draining the queue and delivering the messages, it ensure that the
 * Subscriber is ready to accept/handle the messages.
 *
 * Rolls back the read messages if it receives a SubscriberException from Subscriber.
 *
 */

@Slf4j
public abstract class Subscription implements Runnable {

    @Getter
    private final Queue queue;

    @Getter
    private final Subscriber<?> subscriber;


    Subscription(Queue queue, Subscriber<?> subscriber) {
        this.queue = queue;
        this.subscriber = subscriber;
    }

    @Override
    public void run() {
        try {
            if(!subscriber.isReadyToAcceptMessages()) {
                log.debug("Draining of the '{}' Queue is suspended as the '{}' Subscriber is not ready.", queue.getName(), this.subscriber.getClass().getSimpleName());
                return;
            }

            boolean continueDelivering = true;
            while(!Thread.currentThread().isInterrupted() && continueDelivering) {
                continueDelivering = deliver();
            }
        } catch(Throwable t) {
            // Throwable is caught to ensure that the scheduled subscription continues. This logged and ignored.
            log.error("Unexpected error occurred while delivering messages from '{}' Queue to the '{}' Subscriber.", this.getClass().getSimpleName(), queue.getName(), t);
        }
    }

    protected abstract boolean deliver();

    void rollbackMessagesToQueue(Collection<String> messages) {
        if(messages == null || messages.isEmpty()) {
            return;
        }

        for(String oneMessage : messages) {
            try {
                queue.enqueue(oneMessage);
            } catch(Throwable t) {
                // Log this message string and the exception as we can't do much and continue to execute the loop
                log.error("Skipped delivering the following message to the '{}' Subscriber as an error occurred while placing the message back in the '{}' Queue.\n{}", queue.getName(), this.subscriber.getClass().getSimpleName(), oneMessage, t);
            }
        }
    }
}