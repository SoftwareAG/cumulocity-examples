package c8y.trackeragent.logger;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.utils.TrackerContext;

public class TracelogAppenders {
    
    private final TrackerContext trackerContext;
    private LoggerContext loggerContext;

    public TracelogAppenders(TrackerContext trackerContext) {
        this.trackerContext = trackerContext;
        this.loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    }
    
    public void start() {
        for (DeviceCredentials deviceCredentials : trackerContext.getDeviceCredentials()) {
            start(deviceCredentials.getImei());
        }
    }

    /**
     * TODO run after device bootstrapping
     */
    public void start(String imei) {
        TrackerPlatform devicePlatform = trackerContext.getDevicePlatform(imei);
        TracelogAppender tracelogAppender = new TracelogAppender(devicePlatform, loggerContext);
        String loggerName = PlatformLogger.getLoggerName(imei);
        loggerContext.getLogger(loggerName).addAppender(tracelogAppender);
        tracelogAppender.start();
    }
}
