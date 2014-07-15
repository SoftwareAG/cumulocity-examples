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

import com.cumulocity.tixi.server.model.TixiRequestType;
import com.cumulocity.tixi.server.resources.TixiRequest;
import com.cumulocity.tixi.server.services.TixiRequestFactory;
import com.google.common.io.Closeables;

@Component
public class Device implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(Device.class);

	private final TixiOperationsQueue<TixiRequest> tixiOperationsQueue;
	private final TixiRequestFactory requestFactory;
	private final ScheduledExecutorService executorService;
	private volatile ChunkedOutput<TixiRequest> output;

	@Autowired
	public Device(TixiOperationsQueue<TixiRequest> tixiOperationsQueue, TixiRequestFactory requestFactory) {
		this.tixiOperationsQueue = tixiOperationsQueue;
		this.requestFactory = requestFactory;
		this.executorService = Executors.newScheduledThreadPool(1);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		executorService.scheduleAtFixedRate(new WriteResponseCommand(), 1, 5, TimeUnit.SECONDS);
	}

	public void put(TixiRequest jsonResponse) {
		logger.debug("Enqueued response {}.", jsonResponse);
		tixiOperationsQueue.put(jsonResponse);
	}

	public void put(TixiRequestType requestType) {
		put(requestFactory.create(requestType));
	}

	public void setOutput(ChunkedOutput<TixiRequest> output) {
		logger.info("Setup new output.");
		this.output = output;
	}

	private class WriteResponseCommand implements Runnable {
		public void run() {
			if (output == null) {
				return;
			}
			try {
				TixiRequest jsonResponse = tixiOperationsQueue.take();
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
