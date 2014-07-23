package com.cumulocity.tixi.simulator.config;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.tixi.simulator.client.CloudClient;
import com.cumulocity.tixi.simulator.model.TixiCredentials;


public class Main {
	
	public static final String DEVICE_SERIAL = "tixi_20";
	public static final boolean SCHEDULE_POST_LOG = true;
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	private final Properties props;
	private final CloudClient client;
	
	public static void main(String[] args) throws Exception {
		new Main().startSimulator();
	}
	
	public Main() throws IOException {
		props = new Properties();
		props.load(Main.class.getClassLoader().getResourceAsStream("agent.properties"));
		logger.info("Configuration: " + props);
		
		
		client = new CloudClient(props.getProperty("agent.baseURL"), readCredentials(props));
    }

	private TixiCredentials readCredentials(Properties props) {
	    if(props.containsKey("agent.user")) {
	        TixiCredentials credentials = new TixiCredentials();
	        credentials.user = props.getProperty("agent.user");
	        credentials.password = props.getProperty("agent.password");
	        return credentials;
	    }
        return null;
    }

    public void startSimulator() throws IOException {
		logger.info("Tixi Simulator starts!");
		client.sendRegisterRequest();
		client.sendOpenChannel();
		if(SCHEDULE_POST_LOG) {
			schedulePostLog();
		}
	}

	private void schedulePostLog() {
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
