package com.cumulocity.greenbox.server.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.greenbox.server.model.GreenBoxRequest;
import com.cumulocity.greenbox.server.model.GreenBoxResponse;
import com.cumulocity.greenbox.server.model.GreenBoxSendRequest;
import com.cumulocity.greenbox.server.model.GreenBoxSetupRequest;
import com.cumulocity.greenbox.server.service.DeviceService;

@Path("/send")
public class SendResource {

    @Autowired
    private DeviceService service;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response recive(GreenBoxRequest request) {
        try {
            switch (request.getType()) {
            case SEND:
                send((GreenBoxSendRequest) request);
                break;
            case SETUP:
                setup((GreenBoxSetupRequest) request);
                break;
            default:
                throw new IllegalArgumentException("unsupported command type " + request.getType());
            }
            return Response.ok(GreenBoxResponse.success()).build();
        } catch (Exception ex) {
            return Response.ok(GreenBoxResponse.failure(ex)).build();
        }
    }

    private void setup(GreenBoxSetupRequest request) {

    }

    private void send(GreenBoxSendRequest request) {

    }

}
