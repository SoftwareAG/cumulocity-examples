package com.cumulocity.tixi.server.request.util;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.glassfish.jersey.server.ChunkedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.tixi.server.model.RequestType;
import com.cumulocity.tixi.server.resources.TixiJsonResponse;
import com.cumulocity.tixi.server.services.RequestFactory;
import com.google.common.io.Closeables;

@Component
public class Device implements InitializingBean {
	
	private static final Logger logger = LoggerFactory.getLogger(Device.class);

	private final TixiOperationsQueue<TixiJsonResponse> tixiOperationsQueue;
	private final RequestFactory requestFactory;
	private final ScheduledExecutorService executorService;
	private volatile ChunkedOutput<TixiJsonResponse> output;

	@Autowired
	public Device(TixiOperationsQueue<TixiJsonResponse> tixiOperationsQueue, RequestFactory requestFactory) {
		this.tixiOperationsQueue = tixiOperationsQueue;
		this.requestFactory = requestFactory;
		this.executorService = Executors.newScheduledThreadPool(1);
	}
	
	@Override
    public void afterPropertiesSet() throws Exception {
		executorService.scheduleAtFixedRate(new WriteResponseCommand(), 1, 5, TimeUnit.SECONDS);
    }

	public void put(TixiJsonResponse jsonResponse) {
		logger.debug("Enqueued response {}.", jsonResponse);
		tixiOperationsQueue.put(jsonResponse);
	}

	public void put(RequestType requestType) {
		put(requestFactory.create(requestType));
	}

	public void setOutput(ChunkedOutput<TixiJsonResponse> output) {
		logger.info("Setup new output.");
		this.output = output;
	}

	private class WriteResponseCommand implements Runnable {
		public void run() {
			if(output == null) {
				return;
			}
			try {
				TixiJsonResponse jsonResponse = tixiOperationsQueue.take();
				logger.debug("Send new tixi response {}.", jsonResponse);
				output.write(jsonResponse);
			} catch (IOException e) {
				try {
					Closeables.close(output, true);
				} catch (IOException e1) {
				}
			}
		}
	}
}
