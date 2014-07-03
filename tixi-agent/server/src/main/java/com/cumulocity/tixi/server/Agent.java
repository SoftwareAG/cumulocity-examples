package com.cumulocity.tixi.server;

import com.cumulocity.agent.server.Server;
import com.cumulocity.agent.server.ServerBuilder;
import com.cumulocity.tixi.server.resources.TixiAgentService;

public class Agent {

    public static void main(String[] args) {
        final Server server = ServerBuilder.on(8080)
                .rest()
                .scan("com.cumulocity.tixi.server.resources")
                .build();
        server.start();
    }

}
