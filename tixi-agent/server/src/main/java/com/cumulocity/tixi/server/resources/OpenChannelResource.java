package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.resources.TixiJsonResponse.statusOKJson;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ChunkedOutput;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.tixi.server.model.RequestType;
import com.cumulocity.tixi.server.request.util.Device;
import com.cumulocity.tixi.server.services.DeviceControlService;
import com.cumulocity.tixi.server.services.MessageChannel;
import com.cumulocity.tixi.server.services.MessageChannelContext;
import com.google.common.io.Closeables;

@Path("/openchannel")
public class OpenChannelResource {

    private final DeviceControlService deviceControlService;

    private final Device device;

    @Autowired
    public OpenChannelResource(DeviceControlService deviceControlService, Device device) {
	    this.deviceControlService = deviceControlService;
	    this.device = device;
    }

	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public ChunkedOutput<TixiJsonResponse> open() {
		device.put(statusOKJson());
        device.put(RequestType.EXTERNAL_DATABASE);
        device.put(RequestType.LOG_DEFINITION);
        final ChunkedOutput<TixiJsonResponse> output = new ChunkedOutput<TixiJsonResponse>(TixiJsonResponse.class, "\r\n");
        device.setOutput(output);
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
}
