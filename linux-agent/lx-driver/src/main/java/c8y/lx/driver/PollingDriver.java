/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package c8y.lx.driver;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;

/**
 * Base class for a driver that produces sensor readings. Provides the
 * functionality to regularly poll for readings and to configure polling
 * intervals from device management.
 */
public abstract class PollingDriver implements Driver, Configurable, Runnable {

    protected static final Logger logger = LoggerFactory.getLogger(PollingDriver.class);

    public static final String INTERVAL_PROP = ".interval";

    private final ScheduledExecutorService executorService;

    private volatile ScheduledFuture<?> scheduledFuture;

    private final String pollingProp;

    private final long defaultPollingInterval;
    
    private long actualPollingInterval;

    private Platform platform;

    public PollingDriver(final String type, final String pollingProp, final long defaultPollingInterval) {
        this.executorService = newSingleThreadScheduledExecutor(new NamedThreadFactory(type + "Poller"));
        this.pollingProp = pollingProp + INTERVAL_PROP;
        this.defaultPollingInterval = defaultPollingInterval;
        this.actualPollingInterval = defaultPollingInterval;
    }

    @Override
    public void addDefaults(Properties props) {
        props.setProperty(pollingProp, Long.toString(defaultPollingInterval));
    }

    @Override
    public void configurationChanged(Properties props) {
        try {
            String intervalStr = props.getProperty(pollingProp, Long.toString(defaultPollingInterval));
            actualPollingInterval = Long.parseLong(intervalStr);
            rescheduleMeasurements();
        } catch (NumberFormatException x) {
            logger.warn("Polling interval format issue, reverting to default", x);
            this.actualPollingInterval = defaultPollingInterval;
        }
    }

    private void rescheduleMeasurements() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            scheduledFuture = null;
            scheduleMeasurements();
        }
    }

    @Override
    public void initialize(Platform platform) throws Exception {
        this.platform = platform;
    }

    @Override
    public OperationExecutor[] getSupportedOperations() {
        return new OperationExecutor[0];
    }

    @Override
    public void initializeInventory(ManagedObjectRepresentation mo) {
        // Nothing to do here
    }

    @Override
    public void discoverChildren(ManagedObjectRepresentation mo) {
        // Nothing to do here
    }

    @Override
    public void start() {
        scheduleMeasurements();
    }

    protected Platform getPlatform() {
        return platform;
    }

    private void scheduleMeasurements() {
        if (actualPollingInterval == 0) {
            return;
        }

        if (scheduledFuture != null) {
            return; // already started
        }

        long now = new Date().getTime();
        long initialDelay = computeInitialDelay(now, actualPollingInterval);

        scheduledFuture = executorService.scheduleAtFixedRate(this, initialDelay, actualPollingInterval, MILLISECONDS);
    }

    static long computeInitialDelay(long now, long pollingInterval) {
        long remainder = now % pollingInterval;
        return pollingInterval - remainder;
    }

    private static class NamedThreadFactory implements ThreadFactory {

        private final String threadName;

        public NamedThreadFactory(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, threadName);
        }
    }
}
