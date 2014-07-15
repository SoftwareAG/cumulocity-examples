package com.cumulocity.tixi.simulator.config;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.tixi.simulator.client.CloudClient;


public class Main {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
    	logger.info("Tixi Simulator starts!");
    	
        Properties props = new Properties();
        props.load(Main.class.getClassLoader().getResourceAsStream("agent.properties"));
        logger.info("Configuration: " + props);
        
        final CloudClient client = new CloudClient(props.getProperty("agent.baseURL"));
        client.sendBootstrapRequest();
        client.sendOpenChannel();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(new Runnable() {
            
            @Override
            public void run() {
            	logger.info("Scheduller running...");
                client.postLogFileData();
                
            }
        }, 10, 30, TimeUnit.SECONDS);
    }
}
