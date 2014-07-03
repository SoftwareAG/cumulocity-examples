package com.cumulocity.tixi.server;

import com.cumulocity.agent.server.Server;
import com.cumulocity.agent.server.ServerBuilder;
import com.cumulocity.tixi.server.resources.TixiAgentService;

public class TixiAgent {

    public static void main(String[] args) {
        final Server server = ServerBuilder.on(8080)
                .application("tixi")
                .loadConfiguration("client")
                .rest()
                .scan("com.cumulocity.tixi.server.resources")
                .build();
        server.start();
    }

}
