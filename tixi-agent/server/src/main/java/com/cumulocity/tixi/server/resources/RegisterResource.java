package com.cumulocity.tixi.server.resources;

import static com.cumulocity.model.idtype.GId.asString;
import static com.google.common.base.Strings.isNullOrEmpty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.TixiDeviceCredentails;
import com.cumulocity.tixi.server.services.DeviceService;

@Path("/register")
@Component
public class RegisterResource {
    
    private static final Logger logger = LoggerFactory.getLogger(RegisterResource.class);

    private final DeviceService deviceService;

    @Autowired
    public RegisterResource(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Produces(APPLICATION_JSON)
    @GET
    public Response get(@QueryParam("serial") final String serial, @QueryParam("user") final String user) {
        logger.info("Register resource request from: serial " + serial + " user " + user);
        return isNullOrEmpty(user) ? bootstrap(serial) : standard(serial);
    }

    private Response bootstrap(final String serial) {
        final TixiDeviceCredentails credentials = deviceService.bootstrap(new SerialNumber(serial));
        logger.info("Device for serial {} registerd: {}.", serial, credentials);
        // @formatter:off
        return Response.ok(
                new TixiRequest("REGISTER")
                .set("user", credentials.getUser())
                .set("password", credentials.getPassword())
                .set("deviceID", credentials.getDeviceID())
                ).build();
        // @formatter:on
    }

    private Response standard(final String serial) {
    	// @formatter:off
        final ManagedObjectRepresentation device = deviceService.saveTixiAgent(new SerialNumber(serial));
        return Response.ok(
        		new TixiRequest("REGISTER")
        		.set("deviceID", asString(device.getId())))
                .build();
        // @formatter:on
    }
}
