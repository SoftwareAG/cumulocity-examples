package com.cumulocity.agent.snmp.platform.pubsub.service;

import com.cumulocity.agent.snmp.persistence.Queue;
import com.cumulocity.agent.snmp.platform.config.PlatformProvider;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ScheduledFuture;

/**
 * A PubSub service which publishes to a queue and registers a subscriber,
 * which polls on the queue and passes the messages to the subscriber for handling.
 *
 * @param <Q> Queue to PubSub
 */
@Slf4j
public abstract class PubSub<Q extends Queue> {

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private PlatformProvider platformProvider;

    @Autowired
    private Q queue;

    private ScheduledFuture[] scheduledSubscribers;

    public void publish(String message) {
        queue.enqueue(message);
    }

    public void subscribe(Subscriber subscriber) {

        if(subscriber.isBatchingSupported() && subscriber.getTransmitRateInSeconds() > 0) {
            scheduledSubscribers = new ScheduledFuture[1];
            Subscription newSubscription = new Subscription(subscriber);
            scheduledSubscribers[0] = taskScheduler.scheduleWithFixedDelay(newSubscription, Duration.ofSeconds(subscriber.getTransmitRateInSeconds()));
        }
        else {
            int concurrentSubscriptionsCount = subscriber.getConcurrentSubscriptionsCount();
            scheduledSubscribers = new ScheduledFuture[concurrentSubscriptionsCount];
            Subscription newSubscription = new Subscription(subscriber);
            for(int i = 0; i< concurrentSubscriptionsCount; i++) {
                scheduledSubscribers[i] = taskScheduler.scheduleWithFixedDelay(newSubscription, Duration.ofMillis(1));
            }
        }

        log.debug("{} subscribed to queue {}", subscriber.getClass().getName(), queue.getName());
    }

    public void unsubscribe(Subscriber subscriber) {
        for(ScheduledFuture oneScheduledSubscriber : scheduledSubscribers) {
            oneScheduledSubscriber.cancel(true);
        }

        log.debug("{} unsubscribed to queue {}", subscriber.getClass().getName(), queue.getName());
    }

    private final class Subscription implements Runnable {

        private final Subscriber subscriber;

        private Subscription(Subscriber subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void run() {
            Collection<String> messagesFromQueue = null;
            String oneMessage = null;
            try {
                if(!platformProvider.isPlatformAvailable()) {
                    log.debug("Draining of the '{}' Queue is suspended as the platform is unavailable", queue.getName());
                    return;
                }

                int batchSize = subscriber.getBatchSize();

                if(subscriber.isBatchingSupported() && subscriber.getTransmitRateInSeconds() > 0) {
                    while(!Thread.currentThread().isInterrupted()) {
                        messagesFromQueue = new ArrayList<>(subscriber.getBatchSize());
                        int size = queue.drainTo(messagesFromQueue, batchSize);
                        if(size > 0) {
                            subscriber.onMessages(messagesFromQueue);
                        }

                        if(size < batchSize) {
                            break;
                        }
                    }
                }
                else {
                    while(!Thread.currentThread().isInterrupted()) {
                        oneMessage = null;

                        oneMessage = queue.dequeue();
                        if (oneMessage != null && !oneMessage.isEmpty()) {
                            subscriber.onMessage(oneMessage);
                        } else {
                            break;
                        }
                    }
                }
            } catch(Throwable t) {
                // Unable to publish as the platform is unavailable,
                // so mark the platform as unavailable and put the message(s) already read, back into the queue.

                log.debug("Marking the platform as unavailable.");
                platformProvider.markPlatfromAsUnavailable();

                log.error("Failed to publish the contents of '{}' Queue to the Platform. May be Platform is unavailable." +
                        "\nPlacing the failed messages back in the Queue. Will be published when Platform is back online again.", queue.getName(), t);

                if(oneMessage != null) {
                    rollbackMessagesToQueue(Collections.singletonList(oneMessage));
                }
                else if(messagesFromQueue != null && !messagesFromQueue.isEmpty()) {
                    rollbackMessagesToQueue(messagesFromQueue);
                }
            }
        }

        private void rollbackMessagesToQueue(Collection<String> messages) {
            for(String oneMessage : messages) {
                try {
                    queue.enqueue(oneMessage);
                } catch(Throwable t) {
                    // Log this message string and the exception as we can't do much and continue to execute the loop
                    log.error("Skipped publishing the following message to the Platform, as an error occurred while placing the message back in the '{}' Queue.\n{}", queue.getName(), oneMessage, t);
                }
            }
        }
    }
}
