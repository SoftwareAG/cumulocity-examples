/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.server;

import static java.util.concurrent.Executors.newFixedThreadPool;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.configuration.TrackerConfiguration;

@Component
public class Servers {
    
    private final TrackerConfiguration config;
    private final ListableBeanFactory beanFactory;
    private final ExecutorService executorService = newFixedThreadPool(2);
    
    @Autowired
    public Servers(TrackerConfiguration config, ListableBeanFactory beanFactory) {
        this.config = config;
        this.beanFactory = beanFactory;
    }

    public void startAll() throws IOException {
        startServer(config.getLocalPort1());
        startServer(config.getLocalPort2());
    }

    private void startServer(int localPort) throws IOException {
        TrackerServer server = beanFactory.getBean(TrackerServer.class);
        server.start(localPort);
        executorService.execute(server);
    }        

}
