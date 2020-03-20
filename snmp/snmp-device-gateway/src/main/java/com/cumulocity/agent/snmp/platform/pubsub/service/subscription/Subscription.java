package com.cumulocity.agent.snmp.platform.pubsub.service.subscription;

import com.cumulocity.agent.snmp.persistence.Message;
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

    @Getter
    private final short retryLimit;


    Subscription(Queue queue, Subscriber<?> subscriber, short retryLimit) {
        this.queue = queue;
        this.subscriber = subscriber;
        this.retryLimit = retryLimit;
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
            log.error("Unexpected error occurred while publishing the {} to the Platform.", queue.getName().toLowerCase(), t);
        }
    }

    protected abstract boolean deliver();

    void rollbackMessageToQueue(Message message, Throwable throwable) {
        if(message.getBackoutCount() + 1 > getRetryLimit()) {
            log.error("Skipped publishing the following {} to the Platform, as the failure count for publishing it has exceeded the configured retry limit. Error message: {}.\n{}", getQueue().getName().toLowerCase(), throwable.getMessage(), message.getPayload());
            log.debug(throwable.getMessage(), throwable);
        }
        else {
            if(message.getBackoutCount() == 0) {
                // Log only the first time
                log.warn("Failed to publish the following {} to the Platform, will retry again. Error message: {}.\n{}", getQueue().getName().toLowerCase(), throwable.getMessage(), message.getPayload());
                log.debug(throwable.getMessage(), throwable);
            }
            try {
                queue.backout(message);
            } catch(Throwable t) {
                // Log this message string and the exception as we can't do much and continue to execute the loop
                log.error("Unexpected error occurred while storing the following {} for a retry, hence it will not be retried.\n{}", getQueue().getName().toLowerCase(), message.getPayload(), t);
            }
        }
    }
}