package com.cumulocity.tixi.simulator.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cumulocity.tixi.simulator.client.CloudClient;


public class Main {

    public static void main(String[] args) throws Exception {
        final CloudClient client = new CloudClient();
        client.sendBootstrapRequest();
        client.sendOpenChannel();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(new Runnable() {
            
            @Override
            public void run() {
                client.postLogFileData();
                
            }
        }, 10, 30, TimeUnit.SECONDS);
    }
}
