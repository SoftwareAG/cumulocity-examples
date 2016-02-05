package c8y.trackeragent.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.context.TrackerContext;
import ch.qos.logback.classic.LoggerContext;

@Component
public class TracelogAppenders {
    
    private static Logger logger = LoggerFactory.getLogger(TracelogAppenders.class);
    
    private final TrackerContext trackerContext;
    private LoggerContext loggerContext;

    @Autowired
    public TracelogAppenders(TrackerAgent trackerAgent) {
        this(trackerAgent.getContext());
    }
    
    public TracelogAppenders(TrackerContext trackerContext) {
        this.trackerContext = trackerContext;
        this.loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    }
        
    public void start(String imei) {
        TrackerPlatform devicePlatform = trackerContext.getDevicePlatform(imei);
        TracelogAppender tracelogAppender = new TracelogAppender(devicePlatform, loggerContext);
        String loggerName = PlatformLogger.getLoggerName(imei);
        loggerContext.getLogger(loggerName).addAppender(tracelogAppender);
        tracelogAppender.start();
        logger.info("Started for device {}.", imei);
    }
}
