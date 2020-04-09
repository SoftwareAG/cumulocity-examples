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

package com.cumulocity.agent.snmp.platform.pubsub.service;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.persistence.Message;
import com.cumulocity.agent.snmp.persistence.Queue;
import com.cumulocity.agent.snmp.platform.pubsub.service.subscription.Subscription;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.Subscriber;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class PubSubTest {

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private GatewayProperties gatewayProperties;

    @Mock
    private Queue queue;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    @Mock
    private Subscriber<?> subscriber;

    @InjectMocks
    private PubSubImplForTest pubSub;

    @Captor
    private ArgumentCaptor<Subscription> subscriptionArgumentCaptor;

    @Test
    public void shouldPublishNonNullMessage() {
        String message = "MESSAGE STRING";
        pubSub.publish(message);

        Mockito.verify(queue).enqueue(messageCaptor.capture());
        assertEquals(message, messageCaptor.getValue().getPayload());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThowNPE_whenNullMessageIsPublished() {
        pubSub.publish(null);
    }

    @Test
    public void shouldSubscribeWhenBatchingIsNotSupported() {
        Mockito.when(subscriber.isBatchingSupported()).thenReturn(Boolean.FALSE);
        Mockito.when(gatewayProperties.getGatewayPublishRetryLimit()).thenReturn((short)2);

        int concurrentSubscriptionsCount = 3;
        Mockito.when(subscriber.getConcurrentSubscriptionsCount()).thenReturn(Integer.valueOf(concurrentSubscriptionsCount));

        ScheduledFuture<?> mockScheduledFuture = Mockito.mock(ScheduledFuture.class);
        Mockito.doReturn(mockScheduledFuture).when(taskScheduler).scheduleWithFixedDelay(Mockito.any(Subscription.class), Mockito.any(Duration.class));

        pubSub.subscribe(subscriber);

        Mockito.verify(taskScheduler, Mockito.times(concurrentSubscriptionsCount)).scheduleWithFixedDelay(subscriptionArgumentCaptor.capture(), Mockito.eq(Duration.ofMillis(1)));
        assertEquals(subscriber, subscriptionArgumentCaptor.getValue().getSubscriber());

        assertEquals(concurrentSubscriptionsCount, ((ScheduledFuture[])ReflectionTestUtils.getField(pubSub, "subscriptions")).length);
    }

    @Test
    public void shouldSubscribeWhenBatchingIsNotSupported_withZeroConcurrentSubscriptionsCount() {
        Mockito.when(subscriber.isBatchingSupported()).thenReturn(Boolean.FALSE);

        int concurrentSubscriptionsCount = 0;
        Mockito.when(subscriber.getConcurrentSubscriptionsCount()).thenReturn(Integer.valueOf(concurrentSubscriptionsCount));

        pubSub.subscribe(subscriber);

        Mockito.verifyZeroInteractions(taskScheduler);

        assertNull(((ScheduledFuture[])ReflectionTestUtils.getField(pubSub, "subscriptions")));
    }

    @Test
    public void shouldSubscribeWhenBatchingIsSupported() {
        Mockito.when(subscriber.isBatchingSupported()).thenReturn(Boolean.TRUE);
        Mockito.when(gatewayProperties.getGatewayPublishRetryLimit()).thenReturn((short)2);

        long transmitRateInSeconds = 10;
        Mockito.when(subscriber.getTransmitRateInSeconds()).thenReturn(Long.valueOf(transmitRateInSeconds));

        ScheduledFuture<?> mockScheduledFuture = Mockito.mock(ScheduledFuture.class);
        Mockito.doReturn(mockScheduledFuture).when(taskScheduler).scheduleWithFixedDelay(Mockito.any(Subscription.class), Mockito.any(Duration.class));

        pubSub.subscribe(subscriber);

        Mockito.verify(taskScheduler, Mockito.times(1)).scheduleWithFixedDelay(subscriptionArgumentCaptor.capture(), Mockito.eq(Duration.ofSeconds(transmitRateInSeconds)));
        assertEquals(subscriber, subscriptionArgumentCaptor.getValue().getSubscriber());

        assertEquals(1, ((ScheduledFuture[])ReflectionTestUtils.getField(pubSub, "subscriptions")).length);
    }

    @Test
    public void shouldSubscribeWhenBatchingIsSupported_withZeroTransmitRateInSeconds() {
        Mockito.when(subscriber.isBatchingSupported()).thenReturn(Boolean.TRUE);
        Mockito.when(subscriber.getTransmitRateInSeconds()).thenReturn(Long.valueOf(0));
        Mockito.when(gatewayProperties.getGatewayPublishRetryLimit()).thenReturn((short)2);

        int concurrentSubscriptionsCount = 3;
        Mockito.when(subscriber.getConcurrentSubscriptionsCount()).thenReturn(Integer.valueOf(concurrentSubscriptionsCount));

        ScheduledFuture<?> mockScheduledFuture = Mockito.mock(ScheduledFuture.class);
        Mockito.doReturn(mockScheduledFuture).when(taskScheduler).scheduleWithFixedDelay(Mockito.any(Subscription.class), Mockito.any(Duration.class));

        pubSub.subscribe(subscriber);

        Mockito.verify(taskScheduler, Mockito.times(concurrentSubscriptionsCount)).scheduleWithFixedDelay(subscriptionArgumentCaptor.capture(), Mockito.eq(Duration.ofMillis(1)));
        assertEquals(subscriber, subscriptionArgumentCaptor.getValue().getSubscriber());

        assertEquals(concurrentSubscriptionsCount, ((ScheduledFuture[])ReflectionTestUtils.getField(pubSub, "subscriptions")).length);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIllegalStateExceptionIfAlreadySubscribed() {
        Mockito.when(subscriber.isBatchingSupported()).thenReturn(Boolean.FALSE);
        Mockito.when(gatewayProperties.getGatewayPublishRetryLimit()).thenReturn((short)2);

        int concurrentSubscriptionsCount = 2;
        Mockito.when(subscriber.getConcurrentSubscriptionsCount()).thenReturn(Integer.valueOf(concurrentSubscriptionsCount));

        pubSub.subscribe(subscriber);
        pubSub.subscribe(subscriber);
    }

    @Test
    public void shouldUnsubscribe() {
        ScheduledFuture<?> mockScheduledFuture = Mockito.mock(ScheduledFuture.class);
        ReflectionTestUtils.setField(pubSub, "subscriptions", new ScheduledFuture[] {mockScheduledFuture, mockScheduledFuture});

        pubSub.unsubscribe(subscriber);

        Mockito.verify(mockScheduledFuture, Mockito.times(2)).cancel(true);

        assertNull(((ScheduledFuture[])ReflectionTestUtils.getField(pubSub, "subscriptions")));
    }

    @Test
    public void shouldUnsubscribe_withNullConcurrentSubscriptions() {
        ScheduledFuture<?> mockScheduledFuture = Mockito.mock(ScheduledFuture.class);
        ReflectionTestUtils.setField(pubSub, "subscriptions", null);

        pubSub.unsubscribe(subscriber);

        Mockito.verifyZeroInteractions(mockScheduledFuture);

        assertNull(((ScheduledFuture[])ReflectionTestUtils.getField(pubSub, "subscriptions")));
    }

    private static class PubSubImplForTest extends PubSub<Queue> {
    }
}