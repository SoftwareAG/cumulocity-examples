package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.resources.JsonResponse.statusOKJson;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.tixi.server.model.txml.Log;
import com.cumulocity.tixi.server.request.util.RequestStorage;
import com.cumulocity.tixi.server.services.AgentFileSystem;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

@Path("/senddata")
public class SendDataResource {
    
    private static final Logger log = LoggerFactory.getLogger(SendDataResource.class);

    private final RequestStorage requestStorage; 
    
    private final AgentFileSystem agentFileSystem;

    @Autowired
    public SendDataResource(RequestStorage requestStorage, AgentFileSystem agentFileSystem) {
        this.requestStorage = requestStorage;
        this.agentFileSystem = agentFileSystem;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response senddata(
            @FormDataParam("filename") InputStream fileInputStream,
            @FormDataParam("filename") FormDataContentDisposition contentDispositionHeader, 
            @QueryParam("requestId") String requestId, 
            @QueryParam("serial") String serial,
            @QueryParam("user") String user,
            @QueryParam("password") String password) throws IOException {
        String fileName = agentFileSystem.writeIncomingFile(requestId, fileInputStream);
        Class<?> requestEntityType = getRequestEntity(fileInputStream, requestId);
        return Response.ok(statusOKJson()).build();
    }

    private Class<?> getRequestEntity(InputStream fileInputStream, String requestId) {
        Class<?> entityType = requestStorage.get(requestId);
        if (requestId == null) {
            return Log.class;
        }
        return entityType;
    }

}
