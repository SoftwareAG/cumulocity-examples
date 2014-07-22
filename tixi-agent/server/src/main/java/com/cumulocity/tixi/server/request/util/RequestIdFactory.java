package com.cumulocity.tixi.server.request.util;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

@Component
public class RequestIdFactory {

    private AtomicLong requestId = new AtomicLong(1);

    public Long get() {
        return requestId.getAndIncrement();
    }
}
