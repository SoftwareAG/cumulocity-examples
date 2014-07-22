package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.model.TixiRequestType.EXTERNAL_DATABASE;
import static com.cumulocity.tixi.server.model.TixiRequestType.LOG_DEFINITION;
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

import com.cumulocity.tixi.server.services.ChunkedOutputMessageChannel;
import com.cumulocity.tixi.server.services.DeviceMessageChannelService;

@Path("/openchannel")
public class CommandPipeResource {
    
    private static final Logger logger = LoggerFactory.getLogger(CommandPipeResource.class);

    private final DeviceMessageChannelService deviceMessageChannel;

    @Autowired
    public CommandPipeResource(DeviceMessageChannelService deviceMessageChannelService) {
	    this.deviceMessageChannel = deviceMessageChannelService;
    }

	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public ChunkedOutput<TixiRequest> open(@QueryParam("serial") final String serial, @QueryParam("user") final String user) {
	    logger.info("Open channel request from: serial " + serial + " user " + user);
	    final ChunkedOutput<TixiRequest> output = new ChunkedOutput<TixiRequest>(TixiRequest.class, "\r\n");
        deviceMessageChannel.registerMessageOutput(new ChunkedOutputMessageChannel<>(output));
        deviceMessageChannel.send(statusOK());
        deviceMessageChannel.send(EXTERNAL_DATABASE);
        deviceMessageChannel.send(LOG_DEFINITION);
        return output;
    }
	
	
}
