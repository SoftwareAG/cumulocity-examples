package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.resources.TixiJsonResponse.statusOKJson;

import java.io.InputStream;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.tixi.server.model.txml.Log;
import com.cumulocity.tixi.server.request.util.RequestStorage;
import com.cumulocity.tixi.server.services.AgentFileSystem;
import com.cumulocity.tixi.server.services.handler.TixiXmlService;

@Path("/senddata")
public class SendDataResource {

	private static final Logger logger = LoggerFactory.getLogger(SendDataResource.class);

	private final RequestStorage requestStorage;

	private final AgentFileSystem agentFileSystem;

	private final TixiXmlService tixiService;

	@Autowired
	public SendDataResource(TixiXmlService tixiService, RequestStorage requestStorage, AgentFileSystem agentFileSystem) {
		this.tixiService = tixiService;
		this.requestStorage = requestStorage;
		this.agentFileSystem = agentFileSystem;
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response senddata(@FormDataParam("filename") InputStream fileInputStream,
	        @FormDataParam("filename") FormDataContentDisposition contentDispositionHeader, 
	        @QueryParam("requestId") String requestId,
	        @QueryParam("serial") String serial) {
	    logger.info("Send data request from: serial " + serial);
	    
		handleTixiRequest(fileInputStream, requestId);
		return Response.ok(statusOKJson()).build();
	}

	private void handleTixiRequest(InputStream fileInputStream, String requestId) {
		String fileName = agentFileSystem.writeIncomingFile(requestId, fileInputStream);
		Class<?> requestEntityType = getRequestEntity(requestId);
		if (requestEntityType != null) {
			tixiService.handle(fileName, requestEntityType);
		}
	}

	private Class<?> getRequestEntity(String requestId) {
		if (requestId == null) {
			return Log.class;
		}
		return requestStorage.get(requestId);
	}

}
