package c8y.trackeragent_it.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.Agent;
import com.cumulocity.model.ID;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;

public class AgentCreator {
    
    private static final String xxxpwd = "xxx";
    private static final String TENANT = "management";
    private static final String OWNER = "device_tracker-agent-management";

    private static Logger logger = LoggerFactory.getLogger(AgentCreator.class);
    
    private final IdentityApi identityApi;
    private final InventoryApi inventoryApi;
    
    public static void main(String[] args) {
        CumulocityCredentials credentials = CumulocityCredentials.Builder.cumulocityCredentials("sysadmin", xxxpwd).withTenantId(TENANT).build();
        PlatformImpl platform = new PlatformImpl("https://dev-a.cumulocity.com", credentials);
        new AgentCreator(platform).fire();
    }

    public AgentCreator(Platform platform) {
        identityApi = platform.getIdentityApi();
        inventoryApi = platform.getInventoryApi();
    }

    private ManagedObjectRepresentation fire() {
        ID extId = getAgentExternalId();
        GId gid = tryGetBinding(extId);
        if (gid == null) {
            logger.info("Agent not create yet for tenant " + TENANT + " will create it!");
            ManagedObjectRepresentation agentMo = new ManagedObjectRepresentation();
            agentMo.setType("c8y_TrackerAgent");
            agentMo.setName("Tracker agent");
            agentMo.setOwner(OWNER);
            agentMo.set(new Agent());            
            agentMo = inventoryApi.create(agentMo);
            logger.info("Agent created: {}.", agentMo);
            bind(agentMo, extId);
            return agentMo;
        } else {
            return inventoryApi.get(gid);
        }
    }

    
    public static ID getAgentExternalId() {
        ID extId = new ID("c8y_TrackerAgent");
        extId.setType("c8y_ServerSideAgent");
        return extId;
    }

    public GId tryGetBinding(ID extId) throws SDKException {
        ExternalIDRepresentation eir = null;
        try {
            eir = identityApi.getExternalId(extId);
        } catch (SDKException x) {
            if (x.getHttpStatus() != 404) {
                throw x;
            }
        }
        return eir != null ? eir.getManagedObject().getId() : null;
    }

    public void bind(ManagedObjectRepresentation mo, ID extId) throws SDKException {
        ExternalIDRepresentation eir = new ExternalIDRepresentation();
        eir.setExternalId(extId.getValue());
        eir.setType(extId.getType());
        eir.setManagedObject(mo);
        identityApi.create(eir);
    }


}
