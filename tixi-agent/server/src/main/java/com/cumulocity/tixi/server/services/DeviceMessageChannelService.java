package com.cumulocity.tixi.server.services;

import static com.cumulocity.tixi.server.model.TixiRequestType.HEARTBEAT;

import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.inject.DeviceScope;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.tixi.server.model.TixiRequestType;
import com.cumulocity.tixi.server.resources.TixiRequest;
import com.cumulocity.tixi.server.services.MessageChannel.MessageChannelListener;

@Component
@DeviceScope
public class DeviceMessageChannelService implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(DeviceMessageChannelService.class);

    private BlockingQueue<TixiRequest> requestQueue = new LinkedBlockingQueue<TixiRequest>();

    private TixiRequestFactory requestFactory;
    
    private ScheduledExecutorService executorService;

    private volatile MessageChannel<TixiRequest> output;

    private DeviceContextService deviceContextService;

    protected DeviceMessageChannelService() {
    }

    @Autowired
    public DeviceMessageChannelService(TixiRequestFactory requestFactory, DeviceContextService deviceContextService) {
        this.requestFactory = requestFactory;
        this.deviceContextService = deviceContextService;
        this.executorService = Executors.newScheduledThreadPool(1);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        executorService.scheduleAtFixedRate(deviceContextService.withinContext(new WriteResponseCommand()), 1, 5, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(deviceContextService.withinContext(new SendHeartBeatCommand()), 5, 10, TimeUnit.MINUTES);
    }

    public void send(TixiRequest tixiRequest) {
        log.debug("Enqueued tixiRequest {}.", tixiRequest);
        try {
            requestQueue.put(tixiRequest);
        } catch (InterruptedException e) {
            log.warn("Enqueu  tixi request failed", e);
        }
    }

    public void send(TixiRequestType requestType) {
        send(requestFactory.create(requestType));
    }

    public void registerMessageOutput(MessageChannel<TixiRequest> output) {
        log.info("Registred new output");
        this.output = output;
    }
    
    private class WriteResponseCommand implements Runnable {
        public void run() {
            if (output == null) {
                log.debug("no output defined");
                return;
            }
            try {
                TixiRequest request = requestQueue.take();
                log.debug("Send new tixi request {}.", request);
                output.send(new MessageChannelListener<TixiRequest>() {

                @Override
                public void close(){
                    output = null;
                }

                @Override
                public void failed(TixiRequest message) {
                    requestQueue.add(message);

                }
            }, request);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private class SendHeartBeatCommand implements Runnable {
        public void run() {
            if (output == null) {
                log.debug("no output defined");
                return;
            }
            send(HEARTBEAT);
        }
    }
}
