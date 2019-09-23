package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import com.cumulocity.agent.snmp.platform.pubsub.service.PubSub;
import com.cumulocity.sdk.client.SDKException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collection;

/**
 * Subscriber to handle messages which subscribes/unsubscribes itself as
 * part of the PostConstruct and PreDestroy spring callbacks
 *
 * @param <PS> PubSub service to subscribe to.
 */
@Slf4j
public abstract class Subscriber<PS extends PubSub> {

    @Autowired
    private PS pubSub;


    public boolean isBatchingSupported() {
        return false;
    }

    public int getBatchSize() {
        return 0;
    }

    public void onMessage(String message) {
        try {
            handleMessage(message);
        } catch(SDKException sdke) {
            if (isExceptionDueToInvalidMessage(sdke)) {
                // If the error is caused by an invalid message which is being processed, we will not be able to do much here.
                // Just log the message with the exception details and continue.
                log.error("Message published to the platform is invalid.", sdke);
                log.error("Skipping the processing of the following message");

                // Log the message and return
                log.error("SKIPPED - " + message);
            }
            else {
                throw sdke;
            }
        }
    }

    public void onMessages(Collection<String> messageCollection) {
        try {
            handleMessages(messageCollection);
        } catch(SDKException sdke) {
            if (isExceptionDueToInvalidMessage(sdke)) {
                // If the error is caused by an invalid message which is being processed, we will not be able to do much here.
                // Just log the message with the exception details and continue.
                log.error("Messages published to the platform are invalid.", sdke);
                log.error("Skipping the processing of the following messages");

                // Log the messages and return
                for(String oneMessage:messageCollection) {
                    log.error("SKIPPED - " + oneMessage);
                }
            }
            else {
                throw sdke;
            }
        }
    }

    protected abstract void handleMessage(String message);

    protected void handleMessages(Collection<String> messageCollection) {
        throw new UnsupportedOperationException();
    }

    @PostConstruct
    private void subscribe() {
        pubSub.subscribe(this);
    }

    @PreDestroy
    private void unsubscribe() {
        pubSub.unsubscribe(this);
    }

    private boolean isExceptionDueToInvalidMessage(SDKException sdke) {
        int httpStatus = sdke.getHttpStatus();
        return httpStatus >= HttpStatus.SC_BAD_REQUEST && httpStatus < HttpStatus.SC_INTERNAL_SERVER_ERROR
                && !(httpStatus == HttpStatus.SC_UNAUTHORIZED || httpStatus == HttpStatus.SC_PAYMENT_REQUIRED);
    }
}