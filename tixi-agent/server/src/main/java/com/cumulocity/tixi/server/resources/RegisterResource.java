package com.cumulocity.tixi.server.resources;

import static com.google.common.base.Strings.isNullOrEmpty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.cumulocity.agent.server.annotation.Named;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.TixiDeviceCredentails;
import com.cumulocity.tixi.server.services.TixiDeviceService;

@Path("/register")
@Named
public class RegisterResource {

    private final TixiDeviceService deviceService;

    @Inject
    public RegisterResource(TixiDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Produces(APPLICATION_JSON)
    @GET
    public Response get(@QueryParam("serial") final String serial, @QueryParam("user") final String user) throws Exception {
        return isNullOrEmpty(user) ? bootstrap(serial) : standard(serial);
    }

    private Response bootstrap(final String serial) {
        final TixiDeviceCredentails credentials = deviceService.register(new SerialNumber(serial));

        return Response.ok(
                new JsonResponse("REGISTER").set("user", credentials.getUsername()).set("password", credentials.getPassword())
                        .set("deviceId", credentials.getDeviceId())).build();
    }

    private Response standard(final String serial) {
        return Response.ok(new JsonResponse("REGISTER").set("deviceId", GId.asString(deviceService.findGId(new SerialNumber(serial)))))
                .build();
    }
}
