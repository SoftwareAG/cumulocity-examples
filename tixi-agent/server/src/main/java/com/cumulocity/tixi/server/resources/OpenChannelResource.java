package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.resources.JsonResponse.statusOKJson;

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
import com.cumulocity.tixi.server.services.*;
import com.google.common.io.Closeables;

@Path("/openchannel")
public class OpenChannelResource {

    private final DeviceControlService deviceControlService;
    
    private final TixiOperationsQueue<JsonResponse> tixiOperationsQueue;
    
    private final RequestIdFactory requestIdFactory;

    private final ScheduledExecutorService executorService; 

    @Autowired
    public OpenChannelResource(DeviceControlService deviceControlService, TixiOperationsQueue<JsonResponse> tixiOperationsQueue, RequestIdFactory requestIdFactory) {
        this.deviceControlService = deviceControlService;
        this.tixiOperationsQueue = tixiOperationsQueue;
        this.requestIdFactory = requestIdFactory;
        this.executorService = Executors.newScheduledThreadPool(1);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ChunkedOutput<JsonResponse> open() {
        tixiOperationsQueue.put(statusOKJson());
        tixiOperationsQueue.put(getExternalDBJson());
        tixiOperationsQueue.put(getLogDefinitionJson());
        
        final ChunkedOutput<JsonResponse> output = new ChunkedOutput<JsonResponse>(JsonResponse.class);
        executorService.scheduleAtFixedRate(sendSingleTixiCommand(output), 1, 5, TimeUnit.SECONDS);

        return output;
    }

    private JsonResponse getLogDefinitionJson() {
       return new JsonResponse("TiXML").set("requestId", requestIdFactory.get().toString()).set("parameter",
                "[<GetConfig _=\"LOG/LogDefinition\" ver=\"v\"/>]");
    }

    private JsonResponse getExternalDBJson() {
        return new JsonResponse("TiXML").set("requestId", requestIdFactory.get().toString()).set("parameter",
                "[<GetConfig _=\"PROCCFG/External\" ver=\"v\"/>]");
    }

    private void subscribeOnOperation(final ChunkedOutput<JsonResponse> output) {
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

            private JsonResponse asJsonResponse(OperationRepresentation message) {
                return new JsonResponse("TiXML").set("requestId", GId.asString(message.getId())).set("parameter",
                        message.get("tixi_command"));
            }

            @Override
            public void close() {
                final JsonResponse response = new JsonResponse().set("status", 1l);
                send(output, response, null);
            }

            private void send(final ChunkedOutput<JsonResponse> output, final JsonResponse response, MessageChannelContext context) {
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
    
    private Runnable sendSingleTixiCommand(final ChunkedOutput<JsonResponse> output) {
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
