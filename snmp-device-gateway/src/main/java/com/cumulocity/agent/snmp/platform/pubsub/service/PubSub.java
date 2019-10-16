package com.cumulocity.agent.snmp.platform.pubsub.service;

import com.cumulocity.agent.snmp.persistence.Queue;
import com.cumulocity.agent.snmp.platform.pubsub.service.subscription.BatchMessagesSubscription;
import com.cumulocity.agent.snmp.platform.pubsub.service.subscription.SingleMessageSubscription;
import com.cumulocity.agent.snmp.platform.pubsub.service.subscription.Subscription;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

/**
 * A PubSub service which is used by publisher to publish message to a queue
 * and by subscriber to subscribe to a queue.
 *
 * @param <Q> Queue to PubSub
 */

@Slf4j
public abstract class PubSub<Q extends Queue> {

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private Q queue;

    private ScheduledFuture<?>[] subscriptions;


    public void publish(String message) {
        if(message == null) {
            throw new NullPointerException("message");
        }

        queue.enqueue(message);
    }

    public <PS extends PubSub<?>> void subscribe(Subscriber<PS> subscriber) {
        if(subscriptions != null) {
            throw new IllegalStateException("Duplicate subscriptions.");
        }

        if(subscriber.isBatchingSupported() && subscriber.getTransmitRateInSeconds() > 0) {
            subscriptions = new ScheduledFuture[1];
            Subscription newSubscription = new BatchMessagesSubscription(queue, subscriber);
            subscriptions[0] = taskScheduler.scheduleWithFixedDelay(newSubscription, Duration.ofSeconds(subscriber.getTransmitRateInSeconds()));
        }
        else {
            int concurrentSubscriptionsCount = subscriber.getConcurrentSubscriptionsCount();
            if(concurrentSubscriptionsCount > 0) {
                subscriptions = new ScheduledFuture[concurrentSubscriptionsCount];
                Subscription newSubscription = new SingleMessageSubscription(queue, subscriber);
                for(int i = 0; i< concurrentSubscriptionsCount; i++) {
                    subscriptions[i] = taskScheduler.scheduleWithFixedDelay(newSubscription, Duration.ofMillis(1));
                }
            }
        }

        log.debug("{} subscribed to queue {}", subscriber.getClass().getName(), queue.getName());
    }

    public void unsubscribe(Subscriber<?> subscriber) {
        if(subscriptions == null) {
            return;
        }

        for(ScheduledFuture<?> oneConcurrentSubscription : subscriptions) {
            oneConcurrentSubscription.cancel(true);
        }

        subscriptions = null;

        log.debug("{} unsubscribed to queue {}", subscriber.getClass().getName(), queue.getName());
    }
}
