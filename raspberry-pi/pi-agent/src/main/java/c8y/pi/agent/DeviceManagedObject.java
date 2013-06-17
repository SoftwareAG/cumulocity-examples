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


package c8y.pi.agent;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.ManagedObject;

/**
 * A utility class that simplifies handling devices and their associated external IDs.
 */
public class DeviceManagedObject {
	public DeviceManagedObject(Platform platform, ID extId) {
		this.registry = platform.getIdentityApi();
		this.inventory = platform.getInventoryApi();
		this.extId = extId;
	}
	
	public void createOrUpdate(ManagedObjectRepresentation mo, String defaultName) throws SDKException {
		GId gid = tryGetBinding();
		if (gid == null) {
			mo.setName(defaultName);
			mo = inventory.create(mo);
			bind(mo);
		} else {
			ManagedObject moHandle = inventory.getManagedObject(gid);
			mo = moHandle.update(mo);
		}	
	}

	public GId tryGetBinding() throws SDKException {
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

	public void bind(ManagedObjectRepresentation mo) throws SDKException {
		ExternalIDRepresentation eir = new ExternalIDRepresentation();
		eir.setExternalId(extId.getValue());
		eir.setType(extId.getType());
		eir.setManagedObject(mo);
		registry.create(eir);
	}
	
	
	private IdentityApi registry;
	private InventoryApi inventory;
	private ID extId;
}
