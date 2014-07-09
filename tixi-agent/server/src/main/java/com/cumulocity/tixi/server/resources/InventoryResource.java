package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.resources.TixiJsonResponse.statusOKJson;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.tixi.server.model.RequestType;
import com.cumulocity.tixi.server.request.util.TixiOperationsQueue;
import com.cumulocity.tixi.server.services.RequestFactory;

@Path("/inventory")
public class InventoryResource {

    private final TixiOperationsQueue<TixiJsonResponse> tixiOperationsQueue;
    
    private final RequestFactory requestFactory;

    @Autowired
    public InventoryResource(TixiOperationsQueue<TixiJsonResponse> tixiOperationsQueue, RequestFactory requestFactory) {
        this.tixiOperationsQueue = tixiOperationsQueue;
        this.requestFactory = requestFactory;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response open() {
        tixiOperationsQueue.put(requestFactory.create(RequestType.EXTERNAL_DATABASE));
        tixiOperationsQueue.put(requestFactory.create(RequestType.LOG_DEFINITION));

        return Response.ok(statusOKJson()).build();
    }
}
