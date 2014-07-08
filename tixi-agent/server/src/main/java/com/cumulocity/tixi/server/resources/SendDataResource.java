package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.resources.JsonResponse.statusOKJson;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.tixi.server.components.txml.TXMLUnmarshaller;

@Path("/senddata")
public class SendDataResource {

    private final TXMLUnmarshaller txmlUnmarshaller;

    @Autowired
    public SendDataResource(TXMLUnmarshaller txmlUnmarshaller) {
        this.txmlUnmarshaller = txmlUnmarshaller;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response senddata() {
        
        return Response.ok(statusOKJson()).build();
    }

}
