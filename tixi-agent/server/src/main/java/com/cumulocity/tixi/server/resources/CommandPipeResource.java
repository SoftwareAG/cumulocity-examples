package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.resources.TixiRequest.statusOK;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ChunkedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import c8y.MeasurementRequestOperation;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.tixi.server.model.TixiRequestType;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.request.util.Device;
import com.cumulocity.tixi.server.services.DeviceControlService;
import com.cumulocity.tixi.server.services.MessageChannel;
import com.cumulocity.tixi.server.services.MessageChannelContext;
import com.cumulocity.tixi.server.services.TixiRequestFactory;
import com.cumulocity.tixi.server.services.handler.LogDefinitionRegister;

@Path("/openchannel")
public class CommandPipeResource {
    
    private static final Logger logger = LoggerFactory.getLogger(CommandPipeResource.class);

    private final Device device;
    private final DeviceControlService deviceControlService;
    private final TixiRequestFactory requestFactory;
    private final LogDefinitionRegister LogDefinitionRegister;

    @Autowired
    public CommandPipeResource(Device device, DeviceControlService deviceControlService, TixiRequestFactory requestFactory, LogDefinitionRegister LogDefinitionRegister) {
	    this.device = device;
	    this.deviceControlService = deviceControlService;
		this.requestFactory = requestFactory;
		this.LogDefinitionRegister = LogDefinitionRegister;
    }

	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public ChunkedOutput<TixiRequest> open(@QueryParam("serial") final String serial, @QueryParam("user") final String user) {
	    logger.info("Open channel request from: serial " + serial + " user " + user);
	    ChunkedOutput<TixiRequest> output = new ChunkedOutput<TixiRequest>(TixiRequest.class, "\r\n");
	    device.setOutput(output);
	    device.put(statusOK());
        device.put(TixiRequestType.EXTERNAL_DATABASE);
        device.put(TixiRequestType.LOG_DEFINITION);
        deviceControlService.subscirbe(new OperationMessageChannel());
        return output;
    }
	
	private class OperationMessageChannel implements MessageChannel<OperationRepresentation> {
		
		public void send(MessageChannelContext context, MeasurementRequestOperation measurementRequest) {
			logger.info("Received measurement request {}.", measurementRequest);
			LogDefinition logDefinition = LogDefinitionRegister.getLogDefinition();
			if(logDefinition == null) {
				logger.info("Log definition not availablel skip measurement request.");
				return;
			}
			if(logDefinition.getRecordIds().isEmpty()) {
				logger.warn("Log definition %s has no records!", logDefinition);
				return;
			}
			String recordId = logDefinition.getRecordIds().get(0).getId();
			TixiRequest tixiRequest = requestFactory.createLogRequest(recordId);
			device.put(tixiRequest);
		}
	}
}
