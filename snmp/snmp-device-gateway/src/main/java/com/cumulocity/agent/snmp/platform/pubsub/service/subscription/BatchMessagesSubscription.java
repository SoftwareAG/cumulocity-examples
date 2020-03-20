package com.cumulocity.agent.snmp.platform.pubsub.service.subscription;

import com.cumulocity.agent.snmp.persistence.Message;
import com.cumulocity.agent.snmp.persistence.Queue;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.Subscriber;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BatchMessagesSubscription extends Subscription {

    public BatchMessagesSubscription(Queue queue, Subscriber<?> subscriber, short retryLimit) {
        super(queue, subscriber, retryLimit);
    }

    @Override
    protected boolean deliver() {
        boolean continueDelivering = false;

        int batchSize = getSubscriber().getBatchSize();

        Collection<Message> messagesFromQueue = new ArrayList<>(batchSize);
        int size = getQueue().drainTo(messagesFromQueue, batchSize);
        if(size > 0) {
            try {
                List<String> messageStringsFromQueue = messagesFromQueue.parallelStream().map(Message::getPayload).collect(Collectors.toList());
                getSubscriber().onMessages(messageStringsFromQueue);

                if(size >= batchSize) {
                    continueDelivering = true;
                }
            } catch(Throwable t) {
                // Try to publish them individually, to isolate and rollback only the bad one from the batch
                messagesFromQueue.forEach((oneMessageFromQueue) -> {
                    if(getSubscriber().isReadyToAcceptMessages()) {
                        try {
                            getSubscriber().onMessage(oneMessageFromQueue.getPayload());
                        } catch (Throwable t2) {
                            rollbackMessageToQueue(oneMessageFromQueue, t2);
                        }
                    }
                    else {
                        rollbackMessageToQueue(oneMessageFromQueue, t);
                    }
                });
            }
        }

        return continueDelivering;
    }
}
