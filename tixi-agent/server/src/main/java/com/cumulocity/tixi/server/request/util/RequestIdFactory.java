package com.cumulocity.tixi.server.request.util;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import c8y.inject.DeviceScope;

@Component
@DeviceScope
public class RequestIdFactory {

    private AtomicLong requestId = new AtomicLong(1);

    public Long get() {
        return requestId.getAndIncrement();
    }
}
