package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.resources.TixiRequest.statusOK;
import static org.apache.commons.io.FilenameUtils.removeExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.tixi.server.model.txml.External;
import com.cumulocity.tixi.server.model.txml.Log;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
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
	public Response senddata(@FormDataParam("sendfile") InputStream fileInputStream,
	        @FormDataParam("sendfile") FormDataContentDisposition contentDispositionHeader, 
	        @QueryParam("requestId") String requestId,
	        @QueryParam("serial") String serial) throws IOException {
	    String origFileName = contentDispositionHeader.getFileName();
        logger.info("Send data request: serial " + serial + " requestId " + requestId + " with file: " + origFileName);
		handleTixiRequest(new GZIPInputStream(fileInputStream), removeExtension(origFileName), requestId);
		return Response.ok(statusOK()).build();
	}
	
	private void handleTixiRequest(InputStream fileInputStream, String origFileName, String requestId) {
		Class<?> requestEntityType = getRequestEntity(requestId);
		logger.info("Handled request with id: {} and entity type: {}.", requestId, requestEntityType);
		String fileNamePrefix = asSimpleName(requestEntityType);
		String fileName = agentFileSystem.writeIncomingFile(fileNamePrefix, fileInputStream);
		
		if (requestEntityType == LogDefinition.class) {
			tixiService.handleLogDefinition(fileName);
		} else if (requestEntityType == Log.class) {
		    tixiService.handleLog(fileName, origFileName);
		} else if (requestEntityType == External.class) {
			tixiService.handleExternal(fileName, origFileName);
		} else {
		    logger.warn("Can't handle request type " + requestEntityType);
		}
	}

	private String asSimpleName(Class<?> requestEntityType) {
	    return requestEntityType == null ? null : requestEntityType.getSimpleName();
    }

	private Class<?> getRequestEntity(String requestId) {
		if (requestId == null || requestId.trim().equals("0")) {
			return Log.class;
		}
		return requestStorage.get(requestId);
	}
}
