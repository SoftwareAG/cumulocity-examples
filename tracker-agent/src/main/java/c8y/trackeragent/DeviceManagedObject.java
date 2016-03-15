/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package c8y.trackeragent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.Agent;
import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;

import c8y.trackeragent.devicebootstrap.DeviceCredentials;

/**
 * A utility class that simplifies handling devices and their associated
 * external IDs.
 */
public class DeviceManagedObject {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    protected IdentityApi registry;
    protected InventoryApi inventory;
    protected DeviceContextService contextService;
    protected InventoryRepository inventoryRepository;
    protected String tenant;
    protected DeviceCredentials agentCredentials;

    public DeviceManagedObject(TrackerPlatform platform, 
    		DeviceContextService contextService, 
    		InventoryRepository inventoryRepository,
    		DeviceCredentials agentCredentials) {
		this.agentCredentials = agentCredentials;
		this.registry = platform.getIdentityApi();
        this.inventory = platform.getInventoryApi();
        this.tenant = platform.getTenantId();
        this.contextService = contextService;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Create a managed object if it does not exist, or update it if it exists
     * already. Optionally, link to parent managed object as child device.
     * 
     * @param mo
     *            Representation of the managed object to create or update
     * @param parentId
     *            ID of the parent to link to, or null if no link is needed.
     */
    public boolean createOrUpdate(ManagedObjectRepresentation mo, ID extId, GId parentId) throws SDKException {
        GId gid = tryGetBinding(extId);

        ManagedObjectRepresentation returnedMo;
        returnedMo = (gid == null) ? create(mo, extId, parentId) : update(mo, gid);

        copyProps(returnedMo, mo);

        return gid == null;
    }
    
    public ManagedObjectRepresentation assureTrackerAgentExisting() {
        ID extId = getAgentExternalId();
        GId gid = tryGetBinding(extId);
        if(gid == null) {
            ManagedObjectRepresentation agentMo = new ManagedObjectRepresentation();
            agentMo.setType("c8y_TrackerAgent");
            agentMo.setName("Tracker agent");
            agentMo.set(new Agent());            
            return create(agentMo, extId, null);
        } else {
            return inventory.get(gid);
        }
    }
    
    public boolean updateIfExists(ManagedObjectRepresentation mo, ID extId) throws SDKException {
        GId gid = tryGetBinding(extId);
        if (gid == null) {
            return false;
        }
        ManagedObjectRepresentation returnedMo = update(mo, gid);
        copyProps(returnedMo, mo);
        return true;
    }

    private ManagedObjectRepresentation create(ManagedObjectRepresentation mo, ID extId, final GId parentId) throws SDKException {
        final ManagedObjectRepresentation returnedMo = inventory.create(mo);
        bind(returnedMo, extId);
        if (parentId != null) {
            addChildToAgent(returnedMo, parentId);
        }
        return returnedMo;
    }
    
    private void addChildToAgent(final ManagedObjectRepresentation mo, final GId parentId) {
        try {
            contextService.runWithinContext(new DeviceContext(agentCredentials), new Runnable() {
                
                @Override
                public void run() {
                    inventoryRepository.bindToParent(parentId, mo.getId());
                    
                }
            });
        } catch (Exception e) {
            logger.error("Could not add tracker with id " + mo.getId() + " as child to agent", e);
        }
    }

    private ManagedObjectRepresentation update(ManagedObjectRepresentation mo, GId gid) throws SDKException {
        mo.setName(null); // Don't overwrite user-modified names
        mo.setId(gid);
        return inventory.update(mo);
    }

    private void copyProps(ManagedObjectRepresentation returnedMo, ManagedObjectRepresentation mo) {
        mo.setId(returnedMo.getId());
        mo.setName(returnedMo.getName());
        mo.setSelf(returnedMo.getSelf());
    }
    
    public GId getAgentId() {
        ID agentExternalId = getAgentExternalId();
        return tryGetBinding(agentExternalId);
    }

    public GId tryGetBinding(ID extId) throws SDKException {
        ExternalIDRepresentation eir = null;
        try {
            eir = registry.getExternalId(extId);
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
        registry.create(eir);
    }

    protected InventoryApi getInventory() {
        return inventory;
    }
    
    protected static ID imeiAsId(String imei) {
        ID extId = new ID(imei);
        extId.setType(TrackerDevice.XTID_TYPE);
        return extId;
    }
    
    public boolean existsDevice(String imei) {
        return tryGetBinding(imeiAsId(imei)) != null;
    }
    
    public static ID getAgentExternalId() {
        ID extId = new ID("c8y_TrackerAgent");
        extId.setType("c8y_ServerSideAgent");
        return extId;
    }
}
