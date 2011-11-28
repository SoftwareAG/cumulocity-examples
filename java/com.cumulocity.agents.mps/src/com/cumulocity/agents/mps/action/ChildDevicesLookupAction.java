package com.cumulocity.agents.mps.action;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agents.mps.model.MpsAgent;
import com.cumulocity.agents.mps.model.MpsBridge;
import com.cumulocity.agents.mps.model.MpsDevice;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.agent.action.AgentAction;
import com.cumulocity.sdk.client.Platform;

public class ChildDevicesLookupAction implements AgentAction {

    private static final Logger LOG = LoggerFactory.getLogger(ChildDevicesLookupAction.class);

    private Platform platform;
    
    private MpsAgent agent;

    @Autowired
    public ChildDevicesLookupAction(Platform platform, MpsAgent agent) {
		this.platform = platform;
		this.agent = agent;
	}

	@Override
    public void run() {
        if (agent.getAgentRepresentation() == null) {
            String message = "Agent representation is not known. Cannot check childDevices";
            LOG.error(message);
            throw new RuntimeException(message);
        }
        agent.setDevices(findChildDevicesForParent(agent.getAgentRepresentation()));
    }

    public List<MpsDevice> findChildDevicesForParent(ManagedObjectRepresentation parent) {
    	List<MpsDevice> devices = new ArrayList<MpsDevice>();
        for (ManagedObjectReferenceRepresentation childReferenceRepresentation : parent.getChildDevices().getReferences()) {
            GId childGid = null;
            try {
                childGid = childReferenceRepresentation.getManagedObject().getId();
                ManagedObjectRepresentation childRepresentation = platform.getInventory().getManagedObjectResource(childGid).get();
                MpsDevice mpsDevice = childRepresentation.get(MpsDevice.class);
                if (mpsDevice != null) {
                    LOG.info(format("found mps device: %s", childGid.toString()));
                    mpsDevice.setGlobalId(childGid);
                    MpsBridge mpsBridge = parent.get(MpsBridge.class);
                    if (mpsBridge != null) {
                        LOG.info(format("found mps bridge: %s - setting deviceUrl for device: %s",
                        		parent.getId().toString(), childGid.toString()));
                        mpsDevice.setDeviceUrl(mpsBridge.getDeviceUrl());
                    }
                    devices.add(mpsDevice);
                }
                devices.addAll(findChildDevicesForParent(childRepresentation));
            } catch (Exception e) {
                LOG.error(format("cannot get device information for managedObject: %s", childGid.toString()), e);
            }
        }
        return devices;
    }
}
