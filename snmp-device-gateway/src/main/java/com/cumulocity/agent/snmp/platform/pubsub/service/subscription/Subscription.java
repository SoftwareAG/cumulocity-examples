package com.cumulocity.agent.snmp.platform.pubsub.service.subscription;

import com.cumulocity.agent.snmp.persistence.Queue;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.PlatformPublishException;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.Subscriber;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;


@Slf4j
public abstract class Subscription implements Runnable {

    @Getter
    private final Queue queue;

    @Getter
    private final Subscriber subscriber;


    Subscription(Queue queue, Subscriber subscriber) {
        this.queue = queue;
        this.subscriber = subscriber;
    }

    @Override
    public void run() {
        try {
            if(!subscriber.isReady()) {
                log.debug("Draining of the '{}' Queue is suspended as the subscriber {} is not ready.", queue.getName(), subscriber.getClass().getSimpleName());
                return;
            }

            boolean continueProcessing = true;
            while(!Thread.currentThread().isInterrupted() && continueProcessing) {
                continueProcessing = process();
            }
        } catch(PlatformPublishException ppe) {
            log.error("{} subscriber failed to process the messages from '{}' Queue. Message is put back in the queue for retry.", this.getClass().getSimpleName(), queue.getName(), ppe);
            rollbackMessagesToQueue(ppe.getFailedMessages());
        } catch(Throwable t) {
            // Throwable is caught to ensure that the scheduled subscription continues. This logged and ignored.
            log.error("{} subscriber failed to process the messages from '{}' Queue.", this.getClass().getSimpleName(), queue.getName(), t);
        }
    }

    protected abstract boolean process() throws PlatformPublishException;

    private void rollbackMessagesToQueue(Collection<String> messages) {
        if(messages == null || messages.isEmpty()) {
            return;
        }

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