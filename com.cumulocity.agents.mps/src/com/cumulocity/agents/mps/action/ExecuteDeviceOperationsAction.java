package com.cumulocity.agents.mps.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agents.mps.model.MpsAgent;
import com.cumulocity.agents.mps.model.MpsDevice;
import com.cumulocity.agents.mps.model.MpsRelayEvent;
import com.cumulocity.model.control.Relay;
import com.cumulocity.model.control.Relay.RelayState;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.agent.action.AbstractExecuteDeviceOperationsAction;
import com.cumulocity.sdk.client.Platform;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;

/**
 * Executes the operations on the device.
 */
public class ExecuteDeviceOperationsAction extends AbstractExecuteDeviceOperationsAction<MpsDevice> {

	private static final Logger LOG = LoggerFactory.getLogger(ExecuteDeviceOperationsAction.class);
	
	@Autowired
	public ExecuteDeviceOperationsAction(Platform platform, MpsAgent agent) {
		super(platform, agent);
	}
	
	@Override
	protected boolean isOperationSupported(OperationRepresentation operation) {
		if (operation.get(Relay.class) == null) {
			LOG.warn(String.format("Unknown operation with id: %s.", operation.getId().toJSON()));
			return false;
		}
		
		return true;
	}

	@Override
	protected boolean handleSupportedOperation(OperationRepresentation operation) {
		Relay relay = operation.get(Relay.class);
		RelayState relayState = relay.getRelayState();
		
		if (relayState == null) {
            LOG.warn(String.format("Relay received but no RelayState present. Ignoring. OperationID: %s.", 
            		operation.getId().toJSON()));
            return false;
        }
        
        MpsDevice device = agent.getDevice(operation.getDeviceId());
        if (device == null){
            LOG.warn(String.format("Relay received for unknown meter device GId: %s. OperationID: %s.", 
            		operation.getDeviceId().toJSON(), operation.getId().toString()));
            return false;
        }
        
        if (!executeRelayOperationOnDevice(device, relayState)) {
        	return false;
        }
        
        return addEventToPlatform(operation.getDeviceId(), relayState);
	}
	
	/**
	 * Executes the operation on the device.
	 * @param device the device to execute the operation on.
	 * @param relayState the state to set.
	 * @return <code>true</code> if operation was performed sucessfully.
	 */
	private boolean executeRelayOperationOnDevice(MpsDevice device, RelayState relayState) {
        ClientResponse response = getWebResource(device.getChangeStateUrl(relayState)).post(ClientResponse.class);
        
        if (Status.OK.getStatusCode() == response.getStatus()) {
        	LOG.info("Relay state set sucessfully.");
        	return true;
        } else {
        	LOG.error(String.format("Error setting relay state! Recieved HTTP status %d.", response.getStatus()));
        	return false;
        }
	}
	
	/**
	 * Adds an event about operatin executed on the device.
	 * @param deviceId the GID of the device the opeation was executed on.
	 * @param relayState the state set to the device.
	 * @return <code>true</code> if event was registered sucessfully.
	 */
	private boolean addEventToPlatform(GId deviceId, RelayState relayState) {
        MpsRelayEvent event = new MpsRelayEvent(deviceId, relayState);
        try {
        	platform.getEvent().getEventCollectionResource().create(event);
        	return true;
        } catch (Exception e) {
            LOG.error("Problem posting event", e);
            return false;
        }
	}
	
	protected WebResource getWebResource(String url) {
        Client client = ApacheHttpClient.create();
        client.setFollowRedirects(true);
        WebResource webResource = client.resource(url);
        return webResource;
    }
}
