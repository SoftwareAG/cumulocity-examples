package com.cumulocity.tixi.server;

import static java.nio.file.Files.exists;

import java.nio.file.FileSystems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.cumulocity.agent.server.Server;
import com.cumulocity.agent.server.ServerBuilder;
import com.cumulocity.tixi.server.resources.InventoryResource;
import com.cumulocity.tixi.server.resources.CommandPipeResource;
import com.cumulocity.tixi.server.resources.RegisterResource;
import com.cumulocity.tixi.server.resources.SendDataResource;

public class TixiAgent {
	
	private static final Logger logger = LoggerFactory.getLogger(TixiAgent.class);

    public static void main(String[] args) {
    	configureLogger();
        final Server server = ServerBuilder.on(8088)
                .application("Tixi")
                .loadConfiguration("client")
                .rest()
                .scan("com.cumulocity.tixi.server.resources")
                .scan("com.cumulocity.tixi.server.services")
                .scan("com.cumulocity.tixi.server.request")
                .scan("com.cumulocity.tixi.server.components")
                .component(RegisterResource.class)
                .component(SendDataResource.class)
                .component(CommandPipeResource.class)
                .component(InventoryResource.class)
                .build();
        server.start();
    }
    
    /**
     * TODO: incorporate into agent framework
     */
    private static void configureLogger() {
        String logbackConfig = "/etc/tixi/logback.xml";
        if(!exists(FileSystems.getDefault().getPath("/etc/tixi", "logback.xml"))) {
        	System.err.println("Not logback configuration found: " + logbackConfig + ".");
        	return;
        }
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(logbackConfig);
        } catch (JoranException je) {
            throw new RuntimeException("Cant configure logger from " + logbackConfig, je);
        }
        System.out.println("Log configured from file: " + logbackConfig + ".");
    }

}
