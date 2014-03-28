package c8y.trackeragent.web;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.Main;
import c8y.trackeragent.Server;

@WebListener
public class TrackerAgentServletContextListener implements ServletContextListener {
    
    private static final Logger logger = LoggerFactory.getLogger(TrackerAgentServletContextListener.class);
    
    private final ExecutorService executorService;
    private final Server server;
    

    public TrackerAgentServletContextListener() {
        executorService = Executors.newFixedThreadPool(1);
        server = new Server();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.info("Trakcer agent stoped by web server hook.");
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        server.init();
        executorService.submit(server);
        logger.info("Trakcer agent started by web server hook.");
    }
}
