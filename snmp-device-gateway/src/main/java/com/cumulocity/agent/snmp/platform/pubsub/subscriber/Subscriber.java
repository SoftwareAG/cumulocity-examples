package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import com.cumulocity.agent.snmp.bootstrap.model.BootstrapReadyEvent;
import com.cumulocity.agent.snmp.config.ConcurrencyConfiguration;
import com.cumulocity.agent.snmp.platform.model.GatewayDataRefreshedEvent;
import com.cumulocity.agent.snmp.platform.pubsub.service.PubSub;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.sdk.client.SDKException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

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
    private GatewayDataProvider gatewayDataProvider;

    @Autowired
    private PS pubSub;


    private long transmitRateInSeconds = -1;


    public long getTransmitRateInSeconds() {
        return transmitRateInSeconds;
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

    @EventListener(BootstrapReadyEvent.class)
    void subscribe() {
        pubSub.subscribe(this); // Subscribing for the first time

        this.transmitRateInSeconds = fetchTransmitRateFromGatewayDevice();

        log.debug("{} subscribed to {}.", this.getClass().getName(), pubSub.getClass().getName());
    }

    @EventListener(GatewayDataRefreshedEvent.class)
    void refreshSubscription() {
        if(isBatchingSupported()) {
            long transmitRateFromGatewayDevice = fetchTransmitRateFromGatewayDevice();
            if(transmitRateInSeconds != transmitRateFromGatewayDevice) {
                // Refresh the subscription only when the Transmit Rate
                // has changed for the Subscribers supporting batching
                pubSub.unsubscribe(this);
                pubSub.subscribe(this);

                this.transmitRateInSeconds = transmitRateFromGatewayDevice;

                log.debug("{} refreshed its subscription as the transmit rate changed.", this.getClass().getName());
            }
        }
    }

    @PreDestroy
    void unsubscribe() {
        pubSub.unsubscribe(this);

        log.debug("{} unsubscribed to {}.", this.getClass().getName(), pubSub.getClass().getName());
    }

    private long fetchTransmitRateFromGatewayDevice() {
        return gatewayDataProvider.getGatewayDevice().getSnmpCommunicationProperties().getTransmitRate();
    }

    private boolean isExceptionDueToInvalidMessage(SDKException sdke) {
        int httpStatus = sdke.getHttpStatus();
        return     httpStatus >= HttpStatus.SC_BAD_REQUEST
                && httpStatus < HttpStatus.SC_INTERNAL_SERVER_ERROR
                && !(   httpStatus == HttpStatus.SC_UNAUTHORIZED
                     || httpStatus == HttpStatus.SC_PAYMENT_REQUIRED
                     || httpStatus == HttpStatus.SC_REQUEST_TIMEOUT
                    );
    }
}