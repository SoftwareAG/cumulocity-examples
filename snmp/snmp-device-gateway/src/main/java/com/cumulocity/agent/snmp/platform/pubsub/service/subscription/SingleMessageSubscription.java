/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
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
