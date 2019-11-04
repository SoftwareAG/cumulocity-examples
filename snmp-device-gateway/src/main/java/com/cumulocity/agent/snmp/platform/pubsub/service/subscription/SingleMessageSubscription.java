package com.cumulocity.agent.snmp.platform.pubsub.service.subscription;

import com.cumulocity.agent.snmp.persistence.Message;
import com.cumulocity.agent.snmp.persistence.Queue;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.Subscriber;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SingleMessageSubscription extends Subscription {

    public SingleMessageSubscription(Queue queue, Subscriber<?> subscriber, short retryLimit) {
        super(queue, subscriber, retryLimit);
    }

    @Override
    protected boolean deliver() {
        boolean continueDelivering = false;

        Message messageFromQueue = getQueue().dequeue();
        if (messageFromQueue != null) {
            try {
                getSubscriber().onMessage(messageFromQueue.getPayload());

                continueDelivering = true;
            } catch(Throwable t) { // Throwable is caught to ensure that the message is not lost on any condition
                rollbackMessageToQueue(messageFromQueue, t);
            }
        }

        return continueDelivering;
    }
}
