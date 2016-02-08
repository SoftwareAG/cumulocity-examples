package c8y.trackeragent.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.protocol.mapping.TrackerFactory;
import c8y.trackeragent.utils.TrackerConfiguration;

@Component
public class ServerFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(ServerFactory.class);
    
    private static final int REPORTS_EXECUTOR_POOL_SIZE = 10;
    private static final int REQUESTS_EXECUTOR_POOL_SIZE = 10;

    private final TrackerConfiguration config;
    private final ExecutorService reportsExecutor;
    private final ExecutorService requestsExecutor;
    private final TrackerFactory trackerFactory;
    
    @Autowired
    public ServerFactory(TrackerConfiguration config, TrackerFactory trackerFactory) {
        this.config = config;
        this.trackerFactory = trackerFactory;
        this.reportsExecutor = Executors.newFixedThreadPool(REPORTS_EXECUTOR_POOL_SIZE);
        this.requestsExecutor = Executors.newFixedThreadPool(REQUESTS_EXECUTOR_POOL_SIZE);        
    }
    
    public Server createServer(int localPort) {
        logger.info("Create server for port {}", localPort);
        Server server = new Server(config, trackerFactory, reportsExecutor, requestsExecutor, localPort);
        server.init();
        return server;
    }
    
    

}
