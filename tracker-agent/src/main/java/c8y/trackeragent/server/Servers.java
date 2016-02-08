package c8y.trackeragent.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.utils.TrackerConfiguration;

@Component
public class Servers {
    
    private final ServerFactory serverFactory;
    private final TrackerConfiguration config;
    
    @Autowired
    public Servers(ServerFactory serverFactory, TrackerConfiguration config) {
        this.serverFactory = serverFactory;
        this.config = config;
    }

    public void startAll() {
        startServer(config.getLocalPort1());
        startServer(config.getLocalPort2());
    }

    private void startServer(int localPort) {
        Server server = serverFactory.createServer(localPort);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(server);
    }        

}
