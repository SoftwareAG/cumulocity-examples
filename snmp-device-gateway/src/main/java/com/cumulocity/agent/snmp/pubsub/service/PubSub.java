package com.cumulocity.agent.snmp.pubsub.service;

import com.cumulocity.agent.snmp.persistence.Queue;
import com.cumulocity.agent.snmp.platform.service.SnmpAgentGatewayService;
import com.cumulocity.agent.snmp.pubsub.subscriber.Subscriber;
import com.cumulocity.sdk.client.SDKException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpStatus;
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
 * @param <S> Subscriber which handles the messages
 */
@Slf4j
public abstract class PubSub<Q extends Queue, S extends Subscriber> {

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private SnmpAgentGatewayService snmpAgentGatewayService;

    @Autowired
    private Q queue;

    private ScheduledFuture[] scheduledSubscribers;

    public abstract int getSubscriptionThreadCount();

    public void publish(String message) {
        queue.enqueue(message);
    }

    public void subscribe(S subscriber) {

        int subscriptionThreadCount = getSubscriptionThreadCount();

        // TODO: Get the transmitRateInSeconds from the platform
        long transmitRateInSeconds = 0;

        if(transmitRateInSeconds > 0) {
            scheduledSubscribers = new ScheduledFuture[1];
            Subscription newSubscription = new Subscription(subscriber);
            scheduledSubscribers[0] = taskScheduler.scheduleWithFixedDelay(newSubscription, Duration.ofSeconds(transmitRateInSeconds));
        }
        else {
            scheduledSubscribers = new ScheduledFuture[subscriptionThreadCount];
            Subscription newSubscription = new Subscription(subscriber);
            for(int i = 0; i< subscriptionThreadCount; i++) {
                scheduledSubscribers[i] = taskScheduler.scheduleWithFixedDelay(newSubscription, Duration.ofMillis(10 * subscriptionThreadCount));
            }
        }

        log.debug("{} subscribed to queue {}", subscriber.getClass().getName(), queue.getName());
    }

    public void unsubscribe(S subscriber) {
        for(ScheduledFuture oneScheduledSubscriber : scheduledSubscribers) {
            oneScheduledSubscriber.cancel(true);
        }

        log.debug("{} unsubscribed to queue {}", subscriber.getClass().getName(), queue.getName());
    }

    private final class Subscription implements Runnable {

        private final S subscriber;

        public Subscription(S subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void run() {
            boolean isBatchingSupported = subscriber.isBatchingSupported();
            int batchSize = subscriber.getBatchSize();

            Collection<String> messagesFromQueue = null;
            String oneMessage = null;
            try {
                if(!snmpAgentGatewayService.isPlatformAvailable()) {
                    log.debug("Draining of the '" + queue.getName() + "' Queue is suspended temporarily as the platform is unavailable");

                    snmpAgentGatewayService.waitForPlatformToBeAvailable();

                    return;
                }

                // TODO: Get the transmitRateInSeconds from the platform
                long transmitRateInSeconds = 0;

                if(isBatchingSupported && transmitRateInSeconds > 0) {
                    while(!Thread.currentThread().isInterrupted()) {
                        messagesFromQueue = new ArrayList<>(subscriber.getBatchSize());
                        int size = queue.drainTo(messagesFromQueue, batchSize);
                        if(size > 0) {
                            subscriber.handleBulkMessages(messagesFromQueue);
                        }

                        if(size <= batchSize) {
                            break;
                        }
                    }
                }
                else {
                    while(!Thread.currentThread().isInterrupted()) {
                        oneMessage = null;

                        oneMessage = queue.dequeue();
                        if (oneMessage != null && !oneMessage.isEmpty()) {
                            subscriber.handleMessage(oneMessage);
                        } else {
                            break;
                        }
                    }
                }
            } catch(Throwable t) {
                boolean isExceptionDueToInvalidMessage = false;
                if(t instanceof SDKException) {
                    int httpStatus = ((SDKException) t).getHttpStatus();
                    if (httpStatus >= HttpStatus.SC_BAD_REQUEST && httpStatus < HttpStatus.SC_INTERNAL_SERVER_ERROR
                            && !(httpStatus == HttpStatus.SC_UNAUTHORIZED || httpStatus == HttpStatus.SC_PAYMENT_REQUIRED || httpStatus == HttpStatus.SC_FORBIDDEN)) {
                        isExceptionDueToInvalidMessage = true;
                    }
                }

                if(isExceptionDueToInvalidMessage) {
                    // If the error is caused by an invalid message which is being processed, we will not be able to do much here.
                    // Just log the message with the exception details and continue.

                    log.error("Failed to publish the contents of '" + queue.getName() + "' Queue to the Platform.", t);

                    // Log the messages and continue
                    if(oneMessage != null) {
                        log.error(oneMessage + " - Skipped publishing this message to the Platform.");
                    }
                    else if(messagesFromQueue != null && !messagesFromQueue.isEmpty()) {
                        for(String oneMessageFromQueue : messagesFromQueue) {
                            log.error(oneMessage + " - Skipped publishing this message to the Platform.");
                        }
                    }
                }
                else {
                    if(!(t instanceof InterruptedException)) {
                        // Unable to publish as the platform is unavailable,
                        // so mark the platform as unavailable and put the message(s) already read, back into the queue.

                        log.debug("Marking the platform as unavailable.");
                        snmpAgentGatewayService.markPlatfromAsUnavailable();

                        log.error("Failed to publish the contents of '" + queue.getName() + "' Queue to the Platform. May be Platform is unavailable.", t);
                        log.error("Placing the failed messages back in the '" + queue.getName() + "' Queue. " +
                                "Will be published when Platform is back online again.");

                    }

                    if(oneMessage != null) {
                        rollbackMessagesToQueue(Collections.singletonList(oneMessage));
                    }
                    else if(messagesFromQueue != null && !messagesFromQueue.isEmpty()) {
                        rollbackMessagesToQueue(messagesFromQueue);
                    }
                }
            }
        }

        private void rollbackMessagesToQueue(Collection<String> messages) {
            for(String oneMessage : messages) {
                try {
                    queue.enqueue(oneMessage);
                } catch(Throwable t) {
                    // Log this message string and the exception as we can't do much and continue to execute the loop
                    log.error("Error occurred while placing the message back in the '" + queue.getName() + "' Queue.", t);
                    log.error(oneMessage + " - Skipped publishing this message to the Platform.");
                }
            }
        }
    }
}
