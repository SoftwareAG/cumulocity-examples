package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.model.ManagedObjects.asManagedObject;
import static com.cumulocity.tixi.server.resources.TixiRequest.statusOK;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.idtype.GId;

@Path("/heartbeat")
public class HeartBeatResource {

	private static final Logger logger = LoggerFactory.getLogger(HeartBeatResource.class);
    private final InventoryRepository inventoryRepository;
    private final DeviceContextService contextService;
	
    
    @Autowired
    public HeartBeatResource(InventoryRepository inventoryRepository, DeviceContextService deviceContextService) {
        this.inventoryRepository = inventoryRepository;
        this.contextService = deviceContextService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() {
        GId deviceId = contextService.getCredentials().getDeviceId();
        logger.info("Hearbeat request from device: " + deviceId);
        inventoryRepository.save(asManagedObject(deviceId));
        return Response.ok(statusOK()).build();
    }
}
