package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import lombok.Getter;

import java.util.Collection;

public class PlatformPublishException extends Exception {

    @Getter
    private final Collection<String> failedMessages;

    public PlatformPublishException(Collection<String> failedMessages, Throwable t) {
        super(t);

        this.failedMessages = failedMessages;
    }
}
