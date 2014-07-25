package com.cumulocity.tixi.server.services;

import static com.cumulocity.tixi.server.model.TixiRequestType.HEARTBEAT;

import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
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
public class DeviceMessageChannelService implements InitializingBean,DisposableBean {

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
        this.executorService = Executors.newScheduledThreadPool(2);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        executorService.scheduleAtFixedRate(deviceContextService.withinContext(new SendRequestCommand()), 1, 5, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(deviceContextService.withinContext(new EnqueueHeartBeatRequestCommand()), 1, 1, TimeUnit.MINUTES);
    }
    @Override
    public void destroy() throws Exception {
        executorService.shutdown();
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
        MessageChannel<TixiRequest>  previous = this.output;
        this.output = output;
        if(isChannelActive(previous)) {
            previous.close();
        }
        output.addListener(new MessageChannelListener<TixiRequest>() {
            @Override
            public void failed(TixiRequest message) {
                log.debug("Failed to devlivery request {} ", message);
                requestQueue.add(message);
            }
        });
    }
    private boolean isChannelActive(MessageChannel<TixiRequest> output) {
        return output != null && !output.isClosed();
    }
    
    private class SendRequestCommand implements Runnable {
        public void run() {
            if (!isChannelActive(output)) {
                log.debug("no output defined");
                return;
            }
            final MessageChannel<TixiRequest> channel = output;
            TixiRequest request = requestQueue.poll();
            if (request == null) {
                return;
            }
            
            log.debug("Send new tixi request {}.", request);
            channel.send(request);
        }
    }
    
    private class EnqueueHeartBeatRequestCommand implements Runnable {
        public void run() {
            if (!isChannelActive(output)) {
                log.debug("no output defined");
                return;
            }
            send(HEARTBEAT);
        }

        
    }

  
}
