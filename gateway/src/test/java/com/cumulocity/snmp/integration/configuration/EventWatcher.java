package com.cumulocity.snmp.integration.configuration;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Component
public class EventWatcher implements Closeable {

    private Map<Class, CountDownLatch> latchMap = new ConcurrentHashMap<>();
    private Map<Class, Object> resultMap = new ConcurrentHashMap<>();

    @SneakyThrows
    public <T> T waitFor(Class<T> key) {
        if (!resultMap.containsKey(key)) {
            log.info("Waiting for event " + key.getSimpleName());
            final CountDownLatch latch = new CountDownLatch(1);
            latchMap.put(key, latch);
            final boolean await = latch.await(20, SECONDS);
            if (!await) {
                log.error("Event " + key.getSimpleName() + " didn't arrive");
                throw new IllegalArgumentException(format("Expected event %s didn't arrived", key));
            }
        }
        return (T) resultMap.remove(key);
    }

    @EventListener
    public void onEvent(final Object event) {
        final Class<?> key = event.getClass();
        final CountDownLatch countDownLatch = latchMap.get(key);
        logEvent(event);
        resultMap.put(key, event);
        if (countDownLatch != null) {
            latchMap.remove(key);
            countDownLatch.countDown();
        }
    }

    public void close() {
        clear();
    }

    public void clear() {
        for (CountDownLatch countDownLatch : latchMap.values()) {
            countDownLatch.countDown();
        }
        latchMap.clear();
        resultMap.clear();
    }

    private void logEvent(Object event) {
        if (event.getClass().getCanonicalName().contains("cumulocity")) {
            log.info("Event arrived " + event.getClass().getSimpleName());
        }
    }
}
