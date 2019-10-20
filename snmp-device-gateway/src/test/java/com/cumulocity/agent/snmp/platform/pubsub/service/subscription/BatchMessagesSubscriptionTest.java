package com.cumulocity.agent.snmp.platform.pubsub.service.subscription;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.cumulocity.agent.snmp.persistence.Queue;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.Subscriber;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.SubscriberException;
import com.cumulocity.sdk.client.SDKException;

@RunWith(MockitoJUnitRunner.class)
public class BatchMessagesSubscriptionTest {

    @Mock
    private Queue queue;

    @Mock
    private Subscriber<?> subscriber;

    @InjectMocks
    private BatchMessagesSubscription subscripton;


    @Test
    public void shouldNotProcessIfSubscriberNotReady() {
        Mockito.when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.FALSE);

        subscripton.run();

        Mockito.verify(queue, Mockito.times(1)).getName();
        Mockito.verify(queue, Mockito.times(0)).dequeue();
    }

    @Test
    public void shouldProcessMessagesUntilNoneFound() {
        Mockito.when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);

        int batchSize = 10;
        Mockito.when(subscriber.getBatchSize()).thenReturn(Integer.valueOf(batchSize));
        Mockito.when(queue.drainTo(Mockito.anyCollection(), Mockito.eq(batchSize))).thenReturn(Integer.valueOf(batchSize)).thenReturn(Integer.valueOf(batchSize-1));

        subscripton.run();

        Mockito.verify(queue, Mockito.times(2)).drainTo(Mockito.anyCollection(), Mockito.eq(batchSize));
        try {
            Mockito.verify(subscriber, Mockito.times(2)).onMessages(Mockito.anyCollection());
        } catch (SubscriberException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldRollbackMessagesWhenProcessThrowsSubscriberException() throws SubscriberException {
        Mockito.when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);

        int batchSize = 10;
        Mockito.when(subscriber.getBatchSize()).thenReturn(Integer.valueOf(batchSize));

        final List<String> failedMessages = Arrays.asList("MESSAGE 1", "MESSAGE 2", "MESSAGE 3");
        Mockito.doAnswer(new Answer<Integer>() {
            @Override
            @SuppressWarnings("unchecked")
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                List<String> messagesFromQueue = (List<String>) invocationOnMock.getArguments()[0];
                messagesFromQueue.addAll(failedMessages);

                return Integer.valueOf(messagesFromQueue.size());
            }
        }).when(queue).drainTo(Mockito.anyCollection(), Mockito.eq(batchSize));

        Mockito.doThrow(new SubscriberException(new SDKException("Error processing messages.")))
                    .when(subscriber).onMessages(Mockito.anyCollection());

        subscripton.run();

        Mockito.verify(queue, Mockito.times(failedMessages.size())).enqueue(Mockito.startsWith("MESSAGE "));

        Mockito.verify(queue, Mockito.times(1)).drainTo(Mockito.anyCollection(), Mockito.eq(batchSize));
        Mockito.verify(subscriber, Mockito.times(1)).onMessages(Mockito.anyCollection());
    }

    @Test
    public void shouldRollbackMessagesWhenProcessThrowsSubscriberException_1() throws SubscriberException {
        Mockito.when(subscriber.isReadyToAcceptMessages()).thenReturn(Boolean.TRUE);

        int batchSize = 10;
        Mockito.when(subscriber.getBatchSize()).thenReturn(Integer.valueOf(batchSize));

        final List<String> failedMessages = Arrays.asList("MESSAGE 1", "MESSAGE 2", "MESSAGE 3");
        Mockito.doAnswer(new Answer<Integer>() {
            @Override
            @SuppressWarnings("unchecked")
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                List<String> messagesFromQueue = (List<String>) invocationOnMock.getArguments()[0];
                messagesFromQueue.addAll(failedMessages);

                return Integer.valueOf(messagesFromQueue.size());
            }
        }).when(queue).drainTo(Mockito.anyCollection(), Mockito.eq(batchSize));

        Mockito.doThrow(new SubscriberException(new SDKException("Error processing messages.")))
                .when(subscriber).onMessages(Mockito.anyCollection());

        Mockito.doThrow(new NullPointerException()).when(queue).enqueue(Mockito.eq("MESSAGE 2"));

        subscripton.run();

        Mockito.verify(queue, Mockito.times(failedMessages.size())).enqueue(Mockito.startsWith("MESSAGE "));

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
                List<String> messagesFromQueue = (List<String>) invocationOnMock.getArguments()[0];
                messagesFromQueue.addAll(failedMessages);

                return Integer.valueOf(messagesFromQueue.size());
            }
        }).when(queue).drainTo(Mockito.anyCollection(), Mockito.eq(batchSize));

        Mockito.doThrow(new NullPointerException())
                .when(subscriber).onMessages(Mockito.anyCollection());

        subscripton.run();

        Mockito.verify(queue, Mockito.times(1)).drainTo(Mockito.anyCollection(), Mockito.eq(batchSize));
        Mockito.verify(subscriber, Mockito.times(1)).onMessages(Mockito.anyCollection());
    }
}