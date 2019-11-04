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
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SingleMessageSubscriptionTest {

    @Mock
    private Queue queue;

    @Mock
    private Subscriber<?> subscriber;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    private SingleMessageSubscription subscription;

    @Before
    public void setUp() {
        subscription = new SingleMessageSubscription(queue, subscriber, (short)5);
    }

    @Test
    public void shouldNotProcessIfSubscriberNotReady() {
        when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.FALSE);

        subscription.run();

        verify(queue, times(1)).getName();
        verify(queue, times(0)).dequeue();
    }

    @Test
    public void shouldProcessMessagesUntilNoneFound() {
        when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);

        when(queue.dequeue()).thenReturn(new Message("MESSAGE 1")).thenReturn(new Message("MESSAGE 2")).thenReturn(null);

        subscription.run();

        verify(queue, times(3)).dequeue();
        try {
            verify(subscriber, times(2)).onMessage(startsWith("MESSAGE "));
        } catch (SubscriberException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldRollbackMessagesWhenOnMessageThrowsSubscriberException() throws SubscriberException {
        when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);

        when(queue.getName()).thenReturn("ALARM");
        when(queue.dequeue()).thenReturn(new Message("MESSAGE 1")).thenReturn(new Message("MESSAGE 2")).thenReturn(null);

        doThrow(new SubscriberException("", new SDKException("Error processing messages.")))
                    .when(subscriber).onMessage(eq("MESSAGE 2"));

        subscription.run();

        verify(queue, times(1)).backout(messageCaptor.capture());
        assertTrue(messageCaptor.getValue().getPayload().startsWith("MESSAGE 2"));

        verify(queue, times(2)).dequeue();
        verify(subscriber, times(2)).onMessage(startsWith("MESSAGE "));
    }

    @Test
    public void shouldNotRollbackMessagesWhenOnMessageThrowsSubscriberException_and_backoutCountOfMessage_exceededRetryLimit() throws SubscriberException {
        when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);

        when(queue.getName()).thenReturn("ALARM");
        when(queue.dequeue()).thenReturn(new Message("MESSAGE 1")).thenReturn(new Message("MESSAGE 2", (short)5)).thenReturn(null);

        doThrow(new SubscriberException("", new SDKException("Error processing messages.")))
                .when(subscriber).onMessage(eq("MESSAGE 2"));

        subscription.run();

        verify(queue, times(0)).backout(any(Message.class));

        verify(queue, times(2)).dequeue();
        verify(subscriber, times(2)).onMessage(startsWith("MESSAGE "));
    }


    @Test
    public void shouldRollbackMessagesWhenOnMessageThrowsSubscriberException_2() throws SubscriberException {
        when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);

        when(queue.getName()).thenReturn("ALARM");
        when(queue.dequeue()).thenReturn(new Message("MESSAGE 1")).thenReturn(new Message("MESSAGE 2")).thenReturn(null);

        doThrow(new SubscriberException("", new SDKException("Error processing messages.")))
                .when(subscriber).onMessage(eq("MESSAGE 2"));

        doThrow(new NullPointerException()).when(queue).backout(eq(new Message("MESSAGE 2")));

        subscription.run();

        verify(queue, times(1)).backout(messageCaptor.capture());
        assertTrue(messageCaptor.getValue().getPayload().startsWith("MESSAGE "));

        verify(queue, times(2)).dequeue();
        verify(subscriber, times(2)).onMessage(startsWith("MESSAGE "));
    }

    @Test
    public void shouldCatchThrowableAndReturnGracefully() throws SubscriberException {
        when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);

        when(queue.getName()).thenReturn("ALARM");
        when(queue.dequeue()).thenReturn(new Message("MESSAGE 1")).thenReturn(new Message("MESSAGE 2")).thenReturn(null);

        doThrow(new NullPointerException()).when(subscriber).onMessage(eq("MESSAGE 2"));

        subscription.run();

        verify(queue, times(2)).dequeue();
        verify(subscriber, times(2)).onMessage(startsWith("MESSAGE "));
    }

    @Test
    public void shouldRollbackMessageSuccessfully() {
        // given
        Message message = new Message("SOME MESSAGE");
        when(queue.getName()).thenReturn("ALARM");

        // when
        subscription.rollbackMessageToQueue(message, new SubscriberException("", new NullPointerException()));

        // then
        verify(queue).backout(message);
        verify(queue).getName();
    }

    @Test
    public void shouldNot_RollbackMessage_whenBackoutCountExceedsRetryLimit() {
        // given
        Message message = new Message("SOME MESSAGE", (short)5);
        when(queue.getName()).thenReturn("ALARM");

        // when
        subscription.rollbackMessageToQueue(message, new SubscriberException("", new NullPointerException()));

        // then
        verify(queue, times(0)).backout(message);
        verify(queue).getName();
    }

    @Test
    public void should_handle_exceptionsWhileRollingBackTheMessage() {
        // given
        Message message = new Message("SOME MESSAGE", (short)0);
        when(queue.getName()).thenReturn("ALARM");

        doThrow(new NullPointerException()).when(queue).backout(message);

        // when
        subscription.rollbackMessageToQueue(message, new SubscriberException("", new NullPointerException()));

        // then
        verify(queue, times(1)).backout(message);
        verify(queue, times(2)).getName();
    }
}