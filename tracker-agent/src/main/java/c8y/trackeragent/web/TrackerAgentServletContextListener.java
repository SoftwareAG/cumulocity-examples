package c8y.trackeragent.web;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.Server;
import c8y.trackeragent.utils.ConfigUtils;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

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
        server.destroy();
        logger.info("Trakcer agent stoped by web server hook.");
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            configureLogger();
            server.init();
            executorService.submit(server);
            logger.info("Trakcer agent started by web server hook.");
        } catch (Exception  ex) {
            ex.printStackTrace();
        }
    }

    public static void configureLogger() {
        String logBackConfigFileName = new File(ConfigUtils.get().getConfigFilePath("tracker-agent-logback.xml")).getAbsolutePath();
        // assume SLF4J is bound to logback in the current environment
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(logBackConfigFileName);
        } catch (JoranException je) {
            throw new RuntimeException("Cant configure logger. Are you sure the file " + logBackConfigFileName + " is present?", je);
        }
    }

}
