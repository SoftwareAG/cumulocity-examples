package c8y.trackeragent.service;

import org.springframework.stereotype.Component;

import com.cumulocity.model.Agent;
import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.identity.IdentityApi;

import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;

@Component
public class TenantAgentService {
	
//	private IdentityApi registry;
//	private DeviceCredentialsRepository credentialsRepository;
//	
//    public ManagedObjectRepresentation getAgent() {
//        ID extId = getAgentExternalId();
//        GId gid = tryGetBinding(extId);
//        if(gid == null) {
//            ManagedObjectRepresentation agentMo = new ManagedObjectRepresentation();
//            agentMo.setType("c8y_TrackerAgent");
//            agentMo.setName("Tracker agent");
//            agentMo.set(new Agent());            
//            return create(agentMo, extId, null);
//        } else {
//            return inventory.get(gid);
//        }
//    }
//    
//    public static ID getAgentExternalId() {
//        ID extId = new ID("c8y_TrackerAgent");
//        extId.setType("c8y_ServerSideAgent");
//        return extId;
//    }
//    
//    public GId tryGetBinding(ID extId) throws SDKException {
//        ExternalIDRepresentation eir = null;
//        try {
//            eir = registry.getExternalId(extId);
//        } catch (SDKException x) {
//            if (x.getHttpStatus() != 404) {
//                throw x;
//            }
//        }
//        return eir != null ? eir.getManagedObject().getId() : null;
//    }

}
