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
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.SubscriberException;
import com.cumulocity.sdk.client.SDKException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class BatchMessagesSubscriptionTest {

    @Mock
    private Queue queue;

    @Mock
    private Subscriber<?> subscriber;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    private BatchMessagesSubscription subscription;

    @Before
    public void setUp() {
        subscription = Mockito.spy(new BatchMessagesSubscription(queue, subscriber, (short)5));
    }

    @Test
    public void shouldNotProcessIfSubscriberNotReady() {
        Mockito.when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.FALSE);

        subscription.run();

        Mockito.verify(queue, Mockito.times(1)).getName();
        Mockito.verify(queue, Mockito.times(0)).dequeue();
    }

    @Test
    public void shouldNotProcessIfBatchingNotSupported() throws UnsupportedOperationException, SubscriberException {
    	ResultCaptor<Boolean> resultCaptor = new ResultCaptor<>();

        Mockito.when(queue.getName()).thenReturn("ALARM");

        Mockito.when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);
        Mockito.doThrow(new UnsupportedOperationException("TestException")).when(subscriber).getBatchSize();

        subscription.run();

        Mockito.verify(queue, Mockito.times(0)).drainTo(Mockito.anyCollection(), Mockito.anyInt());
        Mockito.verify(subscriber, Mockito.times(0)).onMessages(Mockito.anyCollection());
    }

    @Test
    public void shouldProcessMessagesUntilNoneFound() throws UnsupportedOperationException {
        Mockito.when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);

        int batchSize = 10;
        Mockito.when(subscriber.getBatchSize()).thenReturn(Integer.valueOf(batchSize));
        Mockito.when(queue.drainTo(Mockito.anyCollection(), Mockito.eq(batchSize))).thenReturn(Integer.valueOf(batchSize)).thenReturn(Integer.valueOf(batchSize-1));

        subscription.run();

        Mockito.verify(queue, Mockito.times(2)).drainTo(Mockito.anyCollection(), Mockito.eq(batchSize));
        try {
            Mockito.verify(subscriber, Mockito.times(2)).onMessages(Mockito.anyCollection());
        } catch (SubscriberException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldRetryAndRollbackOnlyFailingMessages_when_OnMessages_ThrowsSubscriberException() throws SubscriberException {
        Mockito.when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);

        int batchSize = 10;
        Mockito.when(subscriber.getBatchSize()).thenReturn(Integer.valueOf(batchSize));

        final List<String> failedMessages = Arrays.asList("MESSAGE 1", "MESSAGE 2", "MESSAGE 3");
        Mockito.doAnswer(new Answer<Integer>() {
            @Override
            @SuppressWarnings("unchecked")
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                List<Message> messagesFromQueue = (List<Message>) invocationOnMock.getArguments()[0];
                messagesFromQueue.addAll(failedMessages.stream().map(m -> new Message(m)).collect(Collectors.toList()));

                return Integer.valueOf(messagesFromQueue.size());
            }
        }).when(queue).drainTo(Mockito.anyCollection(), Mockito.eq(batchSize));
        Mockito.when(queue.getName()).thenReturn("MEASUREMENT");

        Mockito.doThrow(new SubscriberException("", new SDKException("Error processing messages.")))
                    .when(subscriber).onMessages(Mockito.anyCollection());

        Mockito.doThrow(new SubscriberException("", new SDKException("Error processing one Message.")))
                .when(subscriber).onMessage(failedMessages.get(1));

        subscription.run();

        Mockito.verify(subscriber, Mockito.times(1)).onMessage(Mockito.eq(failedMessages.get(0)));
        Mockito.verify(queue, Mockito.times(0)).backout(new Message(failedMessages.get(0)));

        Mockito.verify(subscriber, Mockito.times(1)).onMessage(Mockito.eq(failedMessages.get(1)));
        Mockito.verify(queue, Mockito.times(1)).backout(new Message(failedMessages.get(1)));

        Mockito.verify(subscriber, Mockito.times(1)).onMessage(Mockito.eq(failedMessages.get(2)));
        Mockito.verify(queue, Mockito.times(0)).backout(new Message(failedMessages.get(2)));

        Mockito.verify(queue, Mockito.times(1)).drainTo(Mockito.anyCollection(), Mockito.eq(batchSize));
        Mockito.verify(subscriber, Mockito.times(1)).onMessages(Mockito.anyCollection());
    }

    @Test
    public void shouldRetryOnlyWhenSubscriberIsReady_when_OnMessages_ThrowsSubscriberException() throws SubscriberException {
        Mockito.when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE).thenReturn(Boolean.FALSE);

        int batchSize = 10;
        Mockito.when(subscriber.getBatchSize()).thenReturn(Integer.valueOf(batchSize));

        final List<String> failedMessages = Arrays.asList("MESSAGE 1", "MESSAGE 2", "MESSAGE 3");
        Mockito.doAnswer(new Answer<Integer>() {
            @Override
            @SuppressWarnings("unchecked")
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                List<Message> messagesFromQueue = (List<Message>) invocationOnMock.getArguments()[0];
                messagesFromQueue.addAll(failedMessages.stream().map(m -> new Message(m)).collect(Collectors.toList()));

                return Integer.valueOf(messagesFromQueue.size());
            }
        }).when(queue).drainTo(Mockito.anyCollection(), Mockito.eq(batchSize));
        Mockito.when(queue.getName()).thenReturn("MEASUREMENT");

        Mockito.doThrow(new SubscriberException("", new SDKException("Error processing messages.")))
                .when(subscriber).onMessages(Mockito.anyCollection());

        subscription.run();

        Mockito.verify(queue, Mockito.times(1)).backout(new Message(failedMessages.get(0)));

        Mockito.verify(queue, Mockito.times(1)).backout(new Message(failedMessages.get(1)));

        Mockito.verify(queue, Mockito.times(1)).backout(new Message(failedMessages.get(2)));

        Mockito.verify(queue, Mockito.times(1)).drainTo(Mockito.anyCollection(), Mockito.eq(batchSize));
        Mockito.verify(subscriber, Mockito.times(1)).onMessages(Mockito.anyCollection());
    }

    @Test
    public void shouldCatchThrowableAndReturnGracefully() throws SubscriberException {
        Mockito.when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);

        int batchSize = 10;
        Mockito.when(subscriber.getBatchSize()).thenReturn(Integer.valueOf(batchSize));

        final List<String> failedMessages = Arrays.asList("MESSAGE 1", "MESSAGE 2", "MESSAGE 3");
        Mockito.doAnswer(new Answer<Integer>() {
            @Override
            @SuppressWarnings("unchecked")
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                List<Message> messagesFromQueue = (List<Message>) invocationOnMock.getArguments()[0];
                messagesFromQueue.addAll(failedMessages.stream().map(m -> new Message(m)).collect(Collectors.toList()));

                return Integer.valueOf(messagesFromQueue.size());
            }
        }).when(queue).drainTo(Mockito.anyCollection(), Mockito.eq(batchSize));

        Mockito.doThrow(new NullPointerException())
                .when(subscriber).onMessages(Mockito.anyCollection());

        subscription.run();

        Mockito.verify(queue, Mockito.times(1)).drainTo(Mockito.anyCollection(), Mockito.eq(batchSize));
        Mockito.verify(subscriber, Mockito.times(1)).onMessages(Mockito.anyCollection());
    }

    public class ResultCaptor<T> implements Answer<T> {
        private T result = null;
        public T getResult() {
            return result;
        }

		@Override
		@SuppressWarnings("unchecked")
        public T answer(InvocationOnMock invocationOnMock) throws Throwable {
            result = (T) invocationOnMock.callRealMethod();
            return result;
        }
    }
}