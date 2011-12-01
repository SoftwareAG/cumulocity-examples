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
import com.cumulocity.sdk.agent.action.ExecuteDeviceOperationsAction.DeviceOperationExecutor;
import com.cumulocity.sdk.agent.action.OperationExecutionResult;
import com.cumulocity.sdk.client.Platform;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;

/**
 * Executes the operations on the device.
 */
public class MpsDeviceOperationExecutor implements DeviceOperationExecutor {

	private static final Logger LOG = LoggerFactory.getLogger(MpsDeviceOperationExecutor.class);
	
	private Platform platform;
	
	private MpsAgent agent;
	
	@Autowired
	public MpsDeviceOperationExecutor(Platform platform, MpsAgent agent) {
		this.platform = platform;
		this.agent = agent;
	}
	
	@Override
	public boolean isOperationSupported(OperationRepresentation operation) {
		if (operation.get(Relay.class) == null) {
			LOG.warn(String.format("Unknown operation with id: %s.", operation.getId().toJSON()));
			return false;
		}
		
		return true;
	}

	@Override
	public OperationExecutionResult handleSupportedOperation(OperationRepresentation operation) {
		Relay relay = operation.get(Relay.class);
		RelayState relayState = relay.getRelayState();
		
		if (relayState == null) {
			String failureReason = String.format("Relay received but no RelayState present. Ignoring. OperationID: %s.", 
            		operation.getId().toJSON());
	        LOG.warn(failureReason);
	        return new OperationExecutionResult(false, failureReason);

        }
        
        MpsDevice device = agent.getDevice(operation.getDeviceId());
        if (device == null){
        	String failureReason = String.format("Relay received for unknown meter device GId: %s. OperationID: %s.", 
            		operation.getDeviceId().toJSON(), operation.getId().toString());
            LOG.warn(failureReason);
            return new OperationExecutionResult(false, failureReason);
        }
        
        OperationExecutionResult executionResult = executeRelayOperationOnDevice(device, relayState); 
        if (!executionResult.isSuccess()) {
        	return executionResult;
        }
        
        return addEventToPlatform(operation.getDeviceId(), relayState);
	}
	
	/**
	 * Executes the operation on the device.
	 * @param device the device to execute the operation on.
	 * @param relayState the state to set.
	 * @return <code>OperationExecutionResult</code> which corresponds result of execution
	 */
	private OperationExecutionResult executeRelayOperationOnDevice(MpsDevice device, RelayState relayState) {
        ClientResponse response = getWebResource(device.getChangeStateUrl(relayState)).post(ClientResponse.class);
        
        if (Status.OK.getStatusCode() == response.getStatus()) {
        	LOG.info("Relay state set sucessfully.");
        	return new OperationExecutionResult(true);
        } else {
        	String failureReason = String.format("Error setting relay state! Recieved HTTP status %d.", response.getStatus());
        	LOG.error(failureReason);
        	return new OperationExecutionResult(false, failureReason);
        }
	}
	
	/**
	 * Adds an event about operatin executed on the device.
	 * @param deviceId the GID of the device the opeation was executed on.
	 * @param relayState the state set to the device.
	 * @return <code>OperationExecutionResult</code> which corresponds result of execution
	 */
	private OperationExecutionResult addEventToPlatform(GId deviceId, RelayState relayState) {
        MpsRelayEvent event = new MpsRelayEvent(deviceId, relayState);
        try {
        	platform.getEvent().getEventCollectionResource().create(event);
        	return new OperationExecutionResult(true);
        } catch (Exception e) {
        	String failureReason = "Problem posting event";
            LOG.error(failureReason, e);
            return new OperationExecutionResult(false, failureReason);
        }
	}
	
	protected WebResource getWebResource(String url) {
        Client client = ApacheHttpClient.create();
        client.setFollowRedirects(true);
        WebResource webResource = client.resource(url);
        return webResource;
    }
}
