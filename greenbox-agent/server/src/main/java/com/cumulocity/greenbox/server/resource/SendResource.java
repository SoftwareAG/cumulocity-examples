package com.cumulocity.greenbox.server.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.greenbox.server.model.GreenBoxRequest;
import com.cumulocity.greenbox.server.model.GreenBoxResponse;
import com.cumulocity.greenbox.server.model.GreenBoxSendRequest;
import com.cumulocity.greenbox.server.model.GreenBoxSetupRequest;
import com.cumulocity.greenbox.server.service.DeviceService;

@Path("/send")
public class SendResource {

    private static final Logger log = LoggerFactory.getLogger(SendResource.class);

    @Autowired
    private DeviceService deviceService;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response recive(GreenBoxRequest request) {
        try {
            if (request instanceof GreenBoxSendRequest) {
                send((GreenBoxSendRequest) request);
            } else if (request instanceof GreenBoxSetupRequest) {
                setup((GreenBoxSetupRequest) request);
            } else {
                throw new IllegalArgumentException("unsupported command type " + request.getType());
            }
            return Response.ok(GreenBoxResponse.success()).build();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return Response.ok(GreenBoxResponse.failure(ex)).build();
        }
    }

    private void setup(GreenBoxSetupRequest request) {
        log.debug("setuping {}", request);
        deviceService.setup(request);
    }

    private void send(GreenBoxSendRequest request) {
        log.debug("processing {}", request);
        deviceService.send(request);
    }

}
