package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.resources.TixiRequest.statusOK;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.tixi.server.model.TixiRequestType;
import com.cumulocity.tixi.server.services.DeviceMessageChannelService;

@Path("/inventory")
public class InventoryResource {

	private static final Logger logger = LoggerFactory.getLogger(InventoryResource.class);
	
    private final DeviceMessageChannelService device;
    
    @Autowired
    public InventoryResource(DeviceMessageChannelService device) {
        this.device = device;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response open() {
    	logger.info("Inventory request received.");
        device.send(TixiRequestType.EXTERNAL_DATABASE);
        device.send(TixiRequestType.LOG_DEFINITION);
        return Response.ok(statusOK()).build();
    }
}
