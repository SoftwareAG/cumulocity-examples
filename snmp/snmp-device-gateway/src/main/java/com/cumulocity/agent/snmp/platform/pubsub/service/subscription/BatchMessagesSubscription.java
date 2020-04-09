/*
 * Copyright © 2012 - 2017 Cumulocity GmbH.
 * Copyright © 2017 - 2020 Software AG, Darmstadt, Germany and/or its licensors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
