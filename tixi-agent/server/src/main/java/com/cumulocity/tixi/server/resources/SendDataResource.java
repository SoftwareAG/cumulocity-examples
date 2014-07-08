package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.resources.JsonResponse.statusOKJson;

import java.io.InputStream;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.tixi.server.components.txml.TXMLUnmarshaller;
import com.cumulocity.tixi.server.services.AgentFileSystem;
import com.cumulocity.tixi.server.services.RequestStorage;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/senddata")
public class SendDataResource {
    
    private static final Logger log = LoggerFactory.getLogger(SendDataResource.class);

    private final TXMLUnmarshaller txmlUnmarshaller;
    
    private final RequestStorage requestStorage; 
    
    private final AgentFileSystem agentFileSystem;

    @Autowired
    public SendDataResource(TXMLUnmarshaller txmlUnmarshaller, RequestStorage requestStorage, AgentFileSystem agentFileSystem) {
        this.txmlUnmarshaller = txmlUnmarshaller;
        this.requestStorage = requestStorage;
        this.agentFileSystem = agentFileSystem;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response senddata(@FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader, 
            @QueryParam("requestId") String requestId, 
            @QueryParam("serial") String serial,
            @QueryParam("user") String user,
            @QueryParam("password") String password) {
        String fileName = agentFileSystem.writeIncomingFile(requestId, fileInputStream);
        Object requestEntity = getRequestEntity(fileInputStream, requestId);
        return Response.ok(statusOKJson()).build();
    }

    private Object getRequestEntity(InputStream fileInputStream, String requestId) {
        Class<?> entityType = requestStorage.get(requestId);
        if (requestId == null || entityType == null) {
            return null;
        }
        try {
            return txmlUnmarshaller.unmarshal(new StreamSource(fileInputStream), entityType);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

}
