package com.cumulocity.greenbox.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreenBoxAgent {

    public static void main(String[] args) {
        // @formatter:off
        final Server server = ServerBuilder.on(8088)
                .application("greenbox")
                .logging("greenbox-agent-server")
                .loadConfiguration("greenbox-agent-server")
                .rest()
                .build();
        server.start();
        // @formatter:on
    }

}
