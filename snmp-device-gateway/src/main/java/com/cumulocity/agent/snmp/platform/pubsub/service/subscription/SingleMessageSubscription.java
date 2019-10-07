package com.cumulocity.agent.snmp.platform.pubsub.service.subscription;

import com.cumulocity.agent.snmp.persistence.Queue;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.PlatformPublishException;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.Subscriber;

public class SingleMessageSubscription extends Subscription {

    public SingleMessageSubscription(Queue queue, Subscriber subscriber) {
        super(queue, subscriber);
    }

    @Override
    protected boolean process() throws PlatformPublishException {
        boolean continueProcessing = true;

        String aMessage = getQueue().dequeue();
        if (aMessage != null && !aMessage.isEmpty()) {
            getSubscriber().onMessage(aMessage);
        } else {
            continueProcessing = false;
        }

        return continueProcessing;
    }
}
