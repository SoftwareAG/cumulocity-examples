package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import com.cumulocity.agent.snmp.config.ConcurrencyConfiguration;
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
    ConcurrencyConfiguration concurrencyConfiguration;

    @Autowired
    private PS pubSub;


    private long currentTransmitRateInSeconds;


    public long getTransmitRateInSeconds() {
        // TODO: Get the transmitRateInSeconds from the platform
        return 0;
    }

    public boolean isBatchingSupported() {
        return false;
    }

    public int getBatchSize() {
        return 200;
    }

    public abstract int getConcurrentSubscriptionsCount();

    public void onMessage(String message) {
        try {
            handleMessage(message);
        } catch(SDKException sdke) {
            if (isExceptionDueToInvalidMessage(sdke)) {
                // If the error is caused by an invalid message which is being processed, we will not be able to do much here.
                // Just log the message with the exception details and continue.
                // Log the message and return
                log.error("Skipped publishing the following invalid message to the Platform.\n{}", message, sdke);
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
                // Log the messages and return
                for(String oneMessage : messageCollection) {
                    log.error("Skipped publishing the following invalid message to the Platform.\n{}", oneMessage, sdke);
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
        this.currentTransmitRateInSeconds = getTransmitRateInSeconds();
        pubSub.subscribe(this);
    }

    @PreDestroy
    private void unsubscribe() {
        pubSub.unsubscribe(this);
    }

//  TODO: @EventListener
    private void refreshSubscription(/* TODO: GatewayDataRefreshedEvent */) {
        if(currentTransmitRateInSeconds != getTransmitRateInSeconds()) {
            pubSub.unsubscribe(this);
            pubSub.subscribe(this);

            log.debug("{} refreshed its subscription as the transmit rate changed.", this.getClass().getName());
        }
    }

    private boolean isExceptionDueToInvalidMessage(SDKException sdke) {
        int httpStatus = sdke.getHttpStatus();
        return httpStatus >= HttpStatus.SC_BAD_REQUEST && httpStatus < HttpStatus.SC_INTERNAL_SERVER_ERROR
                && !(httpStatus == HttpStatus.SC_UNAUTHORIZED || httpStatus == HttpStatus.SC_PAYMENT_REQUIRED);
    }
}