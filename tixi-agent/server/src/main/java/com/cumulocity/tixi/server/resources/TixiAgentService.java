package com.cumulocity.tixi.server.resources;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;

@Path("/")
public class TixiAgentService {

    private final DeviceCredentialsApi deviceCredentials;

    @Inject
    public TixiAgentService(DeviceCredentialsApi deviceCredentials) {
        this.deviceCredentials = deviceCredentials;
    }

    @Path("/register")
    @GET
    public Response bootstrap(@QueryParam("serial") String serial) {
        final DeviceCredentialsRepresentation device = deviceCredentials.pollCredentials(serial, (int) SECONDS.toSeconds(10),
                (int) MINUTES.toSeconds(120));
        return Response.ok(device).build();
    }
}
