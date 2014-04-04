package c8y.trackeragent.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.utils.TrackerContext;
import ch.qos.logback.classic.LoggerContext;

public class TracelogAppenders {
    
    private static Logger logger = LoggerFactory.getLogger(TracelogAppenders.class);
    
    private final TrackerContext trackerContext;
    private LoggerContext loggerContext;

    public TracelogAppenders(TrackerContext trackerContext) {
        this.trackerContext = trackerContext;
        this.loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
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
        logger.info("Started for device {}.", imei);
    }
}
