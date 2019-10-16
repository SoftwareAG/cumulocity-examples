package com.cumulocity.agent.snmp.platform.pubsub.service.subscription;

import com.cumulocity.agent.snmp.persistence.Queue;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.Subscriber;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.SubscriberException;
import com.cumulocity.sdk.client.SDKException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class SingleMessageSubscriptionTest {

    @Mock
    private Queue queue;

    @Mock
    private Subscriber<?> subscriber;

    @InjectMocks
    private SingleMessageSubscription subscription;


    @Test
    public void shouldNotProcessIfSubscriberNotReady() {
        Mockito.when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.FALSE);

        subscription.run();

        Mockito.verify(queue, Mockito.times(1)).getName();
        Mockito.verify(queue, Mockito.times(0)).dequeue();
    }

    @Test
    public void shouldProcessMessagesUntilNoneFound() {
        Mockito.when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);

        Mockito.when(queue.dequeue()).thenReturn("MESSAGE 1").thenReturn("MESSAGE 2").thenReturn(null);

        subscription.run();

        Mockito.verify(queue, Mockito.times(3)).dequeue();
        try {
            Mockito.verify(subscriber, Mockito.times(2)).onMessage(Mockito.startsWith("MESSAGE "));
        } catch (SubscriberException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldRollbackMessagesWhenProcessThrowsPlatformPublishException() throws SubscriberException {
        Mockito.when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);

        Mockito.when(queue.dequeue()).thenReturn("MESSAGE 1").thenReturn("MESSAGE 2").thenReturn(null);

        Mockito.doThrow(new SubscriberException(new SDKException("Error processing messages.")))
                    .when(subscriber).onMessage(Mockito.eq("MESSAGE 2"));

        subscription.run();

        Mockito.verify(queue, Mockito.times(1)).enqueue(Mockito.startsWith("MESSAGE 2"));

        Mockito.verify(queue, Mockito.times(2)).dequeue();
        Mockito.verify(subscriber, Mockito.times(2)).onMessage(Mockito.startsWith("MESSAGE "));
    }


    @Test
    public void shouldRollbackMessagesWhenProcessThrowsPlatformPublishException_2() throws SubscriberException {
        Mockito.when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);

        Mockito.when(queue.dequeue()).thenReturn("MESSAGE 1").thenReturn("MESSAGE 2").thenReturn(null);

        Mockito.doThrow(new SubscriberException(new SDKException("Error processing messages.")))
                .when(subscriber).onMessage(Mockito.eq("MESSAGE 2"));

        Mockito.doThrow(new NullPointerException()).when(queue).enqueue(Mockito.eq("MESSAGE 2"));

        subscription.run();

        Mockito.verify(queue, Mockito.times(1)).enqueue(Mockito.startsWith("MESSAGE "));

        Mockito.verify(queue, Mockito.times(2)).dequeue();
        Mockito.verify(subscriber, Mockito.times(2)).onMessage(Mockito.startsWith("MESSAGE "));
    }

    @Test
    public void shouldCatchThrowableAndReturnGracefully() throws SubscriberException {
        Mockito.when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);

        Mockito.when(queue.dequeue()).thenReturn("MESSAGE 1").thenReturn("MESSAGE 2").thenReturn(null);

        Mockito.doThrow(new NullPointerException()).when(subscriber).onMessage(Mockito.eq("MESSAGE 2"));

        subscription.run();

        Mockito.verify(queue, Mockito.times(2)).dequeue();
        Mockito.verify(subscriber, Mockito.times(2)).onMessage(Mockito.startsWith("MESSAGE "));
    }
}