package com.cumulocity.greenbox.server;

import java.util.logging.Logger;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.cumulocity.agent.server.Server;
import com.cumulocity.agent.server.ServerBuilder;
import com.cumulocity.agent.server.feature.ContextFeature;
import com.cumulocity.agent.server.feature.RepositoryFeature;
import com.cumulocity.greenbox.server.model.json.UrlEncodedMessageBodyHandler;
import com.cumulocity.greenbox.server.resource.SendResource;

public class GreenBoxAgent {

    public static void main(String[] args) {
        // @formatter:off
        final Server server = ServerBuilder.on(8189)
                .application("greenbox")
                .logging("greenbox-agent-server")
                .loadConfiguration("greenbox-agent-server")
                .enable(ContextFeature.class)
                .enable(RepositoryFeature.class)
                .enable(GreenBoxAgentFeature.class)
                .jaxrs()
                .register(JacksonFeature.class)
                .register(UrlEncodedMessageBodyHandler.class)
                .register(SendResource.class)
                .register(logger())
                .build();
        server.start();
        // @formatter:on
    }

    private static LoggingFilter logger() {
        return new LoggingFilter(Logger.getLogger(GreenBoxAgent.class.getName()), 1024 * 1024);
    }
}
