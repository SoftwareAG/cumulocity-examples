package c8y.migration.steps;

import static com.cumulocity.rest.representation.operation.DeviceControlMediaType.NEW_DEVICE_REQUEST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.devicebootstrap.NewDeviceRequestRepresentation;
import com.cumulocity.sdk.client.RestConnector;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApiImpl;

import c8y.migration.BootstrapPlatform;
import c8y.migration.Settings;
import c8y.migration.model.MigrationException;
import c8y.migration.model.TenantMigrationRequest;
import c8y.migration.model.TenantMigrationResponse;

@Component
@Order(value = 10)
public class CreateTenantUserStep extends MigrationStep {

	private static final Logger logger = LoggerFactory.getLogger(CreateTenantUserStep.class);

	private final DeviceCredentialsApi deviceCredentialsApi;
	private final RestConnector restConnector;
	private final Settings settings;
	private final BootstrapPlatform bootstrapPlatform;

	@Autowired
	public CreateTenantUserStep(DeviceCredentialsApi deviceCredentialsApi, RestConnector restConnector,
			Settings settings, BootstrapPlatform bootstrapPlatform) {
		this.deviceCredentialsApi = deviceCredentialsApi;
		this.restConnector = restConnector;
		this.settings = settings;
		this.bootstrapPlatform = bootstrapPlatform;
	}

	public void execute(TenantMigrationRequest req, TenantMigrationResponse response) {
		String tenant = req.getTenant();
		logger.info("Start bootstrapping agent.");
		String newAgentRequestId = "tracker-agent-" + tenant;
		logger.info("newAgentRequestId: {}", newAgentRequestId);
		
		deleteRequestIfExists(newAgentRequestId);
		
		logger.info("Will try create WAITING FOR CONNECTION request");
		createWaitingForConnectionRequest(newAgentRequestId);
		
		logger.info("Will try create PENDING_ACCEPTANCE request");
		transposeToPendingAcceptanceRequest(newAgentRequestId);
		
		logger.info("Will try create ACCEPTED request");
		transposeToAcceptedRequest(newAgentRequestId);
		
		logger.info("Will try get agent credentials");
		DeviceCredentialsRepresentation credentials = getCredentials(newAgentRequestId);
		
		logger.info("Tenant {} agent credentials: user: {}, password: {}", tenant, credentials.getUsername(), credentials.getPassword());
		response.setAgentOwner(credentials);
	}

	private void deleteRequestIfExists(String newAgentRequestId) {
		logger.info("Try delete registration {} if already exists", newAgentRequestId);
		NewDeviceRequestRepresentation representation = new NewDeviceRequestRepresentation();
		representation.setId(newAgentRequestId);
		representation.setSelf(settings.getC8yHost() + "/" + DeviceCredentialsApiImpl.DEVICE_REQUEST_URI  + "/"+ newAgentRequestId);
		try {
			deviceCredentialsApi.delete(representation);
		} catch (Exception ex) {
			logger.info("Exception occured trying delete {} request; probably it didnt exist; ignore it: {}",
					newAgentRequestId, ex.getMessage());
		}
	}

	private void createWaitingForConnectionRequest(final String newAgentRequestId) {
		NewDeviceRequestRepresentation representation = new NewDeviceRequestRepresentation();
		representation.setId(newAgentRequestId);
		String path = settings.getC8yHost() + "/devicecontrol/newDeviceRequests";
		try {
			restConnector.post(path, NEW_DEVICE_REQUEST, representation);
			logger.info("Agent is in status WAITING_FOR_CONNECTION");
		} catch (SDKException e) {
			throw new MigrationException(e);
		}
	}

	private void transposeToPendingAcceptanceRequest(final String newAgentRequestId) {
		try {
			bootstrapPlatform.getDeviceCredentialsApi().pollCredentials(newAgentRequestId);
			logger.info("Tenant agent is in status PENDING_ACCEPTANCE");
		} catch (SDKException e) {
			if (e.getHttpStatus() != 404) {
				throw new MigrationException(e);
			}
		}
	}

	private void transposeToAcceptedRequest(String newAgentRequestId) {
		try {
			NewDeviceRequestRepresentation representation = new NewDeviceRequestRepresentation();
			representation.setStatus("ACCEPTED");
			String path = settings.getC8yHost() + "/devicecontrol/newDeviceRequests" + "/" + newAgentRequestId;
			restConnector.put(path, NEW_DEVICE_REQUEST, representation);
			logger.info("Tenant agent is in status ACCEPTED");
		} catch (SDKException ex) {
			throw new MigrationException(ex);
		}
	}
	
	private DeviceCredentialsRepresentation getCredentials(final String newAgentRequestId) {
		try {
			return bootstrapPlatform.getDeviceCredentialsApi().pollCredentials(newAgentRequestId);
		} catch (SDKException e) {
			throw new MigrationException(e);
		}
	}


}
