package com.cumulocity.tixi.server.resources;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ChunkedOutput;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.tixi.server.services.DeviceControlService;
import com.cumulocity.tixi.server.services.MessageChannel;
import com.cumulocity.tixi.server.services.MessageChannelContext;
import com.google.common.io.Closeables;

@Path("/openchannel")
public class OpenChannelResource {

    private final DeviceControlService deviceControlService;

    @Inject
    public OpenChannelResource(DeviceControlService deviceControlService) {
        this.deviceControlService = deviceControlService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ChunkedOutput<JsonResponse> open() {
        final ChunkedOutput<JsonResponse> output = new ChunkedOutput<JsonResponse>(JsonResponse.class);
        deviceControlService.subscirbe(new MessageChannel<OperationRepresentation>() {
            boolean initialized = false;

            @Override
            public void send(MessageChannelContext context, OperationRepresentation message) {
                if (!initialized) {
                    send(output, new JsonResponse().set("status", 0l), context);
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

        return output;
    }
}
