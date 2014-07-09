package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.resources.TixiJsonResponse.statusOKJson;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ChunkedOutput;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.tixi.server.model.RequestType;
import com.cumulocity.tixi.server.request.util.TixiOperationsQueue;
import com.cumulocity.tixi.server.services.DeviceControlService;
import com.cumulocity.tixi.server.services.MessageChannel;
import com.cumulocity.tixi.server.services.MessageChannelContext;
import com.cumulocity.tixi.server.services.RequestFactory;
import com.google.common.io.Closeables;

@Path("/openchannel")
public class OpenChannelResource {

    private final DeviceControlService deviceControlService;
    
    private final TixiOperationsQueue<TixiJsonResponse> tixiOperationsQueue;

    private final ScheduledExecutorService executorService; 
        
    private final RequestFactory requestFactory;

    @Autowired
    public OpenChannelResource(DeviceControlService deviceControlService, TixiOperationsQueue<TixiJsonResponse> tixiOperationsQueue, RequestFactory requestFactory) {
        this.deviceControlService = deviceControlService;
        this.tixiOperationsQueue = tixiOperationsQueue;
        this.requestFactory = requestFactory;
        this.executorService = Executors.newScheduledThreadPool(1);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ChunkedOutput<TixiJsonResponse> open() {
        tixiOperationsQueue.put(statusOKJson());
        tixiOperationsQueue.put(requestFactory.create(RequestType.EXTERNAL_DATABASE));
        tixiOperationsQueue.put(requestFactory.create(RequestType.LOG_DEFINITION));
        
        final ChunkedOutput<TixiJsonResponse> output = new ChunkedOutput<TixiJsonResponse>(TixiJsonResponse.class, "\r\n");
        executorService.scheduleAtFixedRate(sendSingleTixiCommand(output), 1, 5, TimeUnit.SECONDS);

        return output;
    }

    private void subscribeOnOperation(final ChunkedOutput<TixiJsonResponse> output) {
        deviceControlService.subscirbe(new MessageChannel<OperationRepresentation>() {
            boolean initialized = false;

            @Override
            public void send(MessageChannelContext context, OperationRepresentation message) {
                if (!initialized) {
                    send(output, statusOKJson(), context);
                    initialized = true;
                }
                send(output, asJsonResponse(message), context);
            }

            private TixiJsonResponse asJsonResponse(OperationRepresentation message) {
                return new TixiJsonResponse("TiXML").set("requestId", GId.asString(message.getId())).set("parameter",
                        message.get("tixi_command"));
            }

            @Override
            public void close() {
                final TixiJsonResponse response = new TixiJsonResponse().set("status", 1l);
                send(output, response, null);
            }

            private void send(final ChunkedOutput<TixiJsonResponse> output, final TixiJsonResponse response, MessageChannelContext context) {
                try {

                    output.write(response);
                } catch (IOException e) {
                    try {
                        Closeables.close(context, true);
                        Closeables.close(output, true);
                    } catch (IOException e1) {
                    }
                }
            }
        });
    }
    
    private Runnable sendSingleTixiCommand(final ChunkedOutput<TixiJsonResponse> output) {
        return new Runnable() {
            
            @Override
            public void run() {
                try {
                    output.write(tixiOperationsQueue.take());
                } catch (IOException e) {
                    try {
                        Closeables.close(output, true);
                    } catch (IOException e1) {
                    }
                }
                
            }
        };
    }
}
