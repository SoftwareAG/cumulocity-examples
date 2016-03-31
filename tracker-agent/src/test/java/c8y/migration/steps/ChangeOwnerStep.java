package c8y.migration.steps;

import static c8y.trackeragent.device.TrackerDevice.getAgentExternalId;
import static c8y.trackeragent.device.TrackerDevice.imeiAsId;
import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.ID;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;

import c8y.migration.Settings;
import c8y.migration.model.DeviceMigrationRequest;
import c8y.migration.model.DeviceMigrationResponse;
import c8y.migration.model.MigrationException;
import c8y.migration.model.TenantMigrationRequest;
import c8y.migration.model.TenantMigrationResponse;
import c8y.trackeragent.device.TrackerDevice;

@Component
@Order(value = 20)
public class ChangeOwnerStep extends MigrationStep {

	private static final Logger logger = LoggerFactory.getLogger(ChangeOwnerStep.class);

	private final InventoryRepository inventoryRepository;
	private final Settings settings;

	@Autowired
	public ChangeOwnerStep(InventoryRepository inventoryRepository, Settings settings) {
		this.inventoryRepository = inventoryRepository;
		this.settings = settings;
	}

	@Override
	public void execute(TenantMigrationRequest req, TenantMigrationResponse response) {
		String newOwner = response.getAgentOwner().getUsername();
		List<DeviceMigrationRequest> devices = req.getDevices();
		Platform platform = platform(req.getTenant(), devices.get(0));
		ID agentExternalId = getAgentExternalId();
		logger.info("Will change owner for agent {}", agentExternalId);
		changeOwner(agentExternalId, newOwner, platform);
		for (DeviceMigrationRequest device : devices) {
			ID deviceExternalId = imeiAsId(device.getImei());
			logger.info("Will change owner for device {}", deviceExternalId);
			platform = platform(req.getTenant(), device);
			changeOwner(deviceExternalId, newOwner, platform);
			DeviceMigrationResponse deviceResponse = new DeviceMigrationResponse(device.getImei());
			response.getDeviceResponses().add(deviceResponse);
		}
	}

	private void changeOwner(ID externalId, String newOwner, Platform platform) {
		GId globalId = asGlobalId(externalId, platform);
		ManagedObjectRepresentation mo = inventoryRepository.findById(globalId);
		logger.info("change owner from {} to {}", mo.getOwner(), newOwner);
		ManagedObjectRepresentation agentUpdate = new ManagedObjectRepresentation();
		agentUpdate.setId(globalId);
		agentUpdate.setOwner(newOwner);
		try {
			inventoryRepository.save(agentUpdate);
			logger.info("DONE");
		} catch (Exception ex) {
			throw new MigrationException(ex);
		}

	}

	public GId asGlobalId(ID externalId, Platform platform) {
		logger.info("Will try get global id for external id {}", externalId);
		ExternalIDRepresentation externalIdRep = platform.getIdentityApi().getExternalId(externalId);
		if (externalIdRep == null) {
			throw new MigrationException("There is no id mapping for " + externalId);
		}
		GId result = externalIdRep.getManagedObject().getId();
		logger.info("Global id is: {}", result);
		return result;
	}

	private Platform platform(String tenant, DeviceMigrationRequest device) {
		CumulocityCredentials credentials = cumulocityCredentials(device.getUser(), device.getPassword())
				.withTenantId(tenant).build();
		return new PlatformImpl(settings.getC8yHost(), credentials);
	}

}
