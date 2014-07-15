package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.resources.TixiRequest.statusOK;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.tixi.server.model.RequestType;
import com.cumulocity.tixi.server.request.util.Device;

@Path("/inventory")
public class InventoryResource {

    private final Device device;
    
    @Autowired
    public InventoryResource(Device device) {
        this.device = device;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response open() {
        device.put(RequestType.EXTERNAL_DATABASE);
        device.put(RequestType.LOG_DEFINITION);

        return Response.ok(statusOK()).build();
    }
}
