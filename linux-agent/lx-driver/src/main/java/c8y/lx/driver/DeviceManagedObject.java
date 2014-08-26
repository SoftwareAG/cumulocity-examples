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
package c8y.lx.driver;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;

/**
 * A utility class that simplifies handling devices and their associated
 * external IDs.
 */
public class DeviceManagedObject {

    private final IdentityApi registry;

    private final InventoryApi inventory;

    public DeviceManagedObject(Platform platform) {
        this.registry = platform.getIdentityApi();
        this.inventory = platform.getInventoryApi();
    }

    /**
     * Create a managed object if it does not exist, or update it if it exists
     * already. Optionally, link to parent managed object as child device.
     *
     * @param mo       Representation of the managed object to create or update
     * @param parentId ID of the parent to link to, or null if no link is needed.
     */
    public boolean createOrUpdate(ManagedObjectRepresentation mo, ID extId, GId parentId) {
        GId gid = tryGetBinding(extId);

        ManagedObjectRepresentation returnedMo;
        returnedMo = (gid == null) ? create(extId, mo) : update(gid, mo);

        if (parentId != null) {
            ManagedObjectRepresentation handle = new ManagedObjectRepresentation();
            handle.setId(returnedMo.getId());
            handle.setSelf(returnedMo.getSelf());
            ManagedObjectReferenceRepresentation moRef = new ManagedObjectReferenceRepresentation();
            moRef.setManagedObject(handle);
            inventory.getManagedObjectApi(parentId).addChildDevice(moRef);
        }

        copyProps(returnedMo, mo);

        return gid == null;
    }

    private ManagedObjectRepresentation create(ID extId, ManagedObjectRepresentation mo) {
        ManagedObjectRepresentation returnedMo;
        returnedMo = inventory.create(mo);
        bind(returnedMo, extId);
        return returnedMo;
    }

    private ManagedObjectRepresentation update(GId gid, ManagedObjectRepresentation mo) {
        mo.setId(gid);
        mo.setName(null); // Don't overwrite user-modified names
        return inventory.update(mo);
    }

    private void copyProps(ManagedObjectRepresentation returnedMo, ManagedObjectRepresentation mo) {
        mo.setId(returnedMo.getId());
        mo.setName(returnedMo.getName());
        mo.setSelf(returnedMo.getSelf());
        mo.setAttrs(returnedMo.getAttrs());
    }

    public GId tryGetBinding(ID extId) {
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

    public void bind(ManagedObjectRepresentation mo, ID extId) {
        ExternalIDRepresentation eir = new ExternalIDRepresentation();
        eir.setExternalId(extId.getValue());
        eir.setType(extId.getType());
        eir.setManagedObject(mo);
        registry.create(eir);
    }
}
