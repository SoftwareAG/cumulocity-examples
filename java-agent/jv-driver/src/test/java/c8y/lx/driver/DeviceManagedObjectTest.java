package c8y.lx.driver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.ManagedObject;

public class DeviceManagedObjectTest {
	
	private Platform platform = mock(Platform.class);
	private IdentityApi registry = mock(IdentityApi.class);
	private InventoryApi inventory = mock(InventoryApi.class);
	private ManagedObject moHandle = mock(ManagedObject.class);

	private DeviceManagedObject dmo;
	private ID extId = new ID("myId");
	private GId gid = new GId("123");
	private GId parentId = new GId("parent");
	private ManagedObjectRepresentation createdMo = new ManagedObjectRepresentation();
	
	@Before
	public void setup() throws SDKException {
		when(platform.getIdentityApi()).thenReturn(registry);
		when(platform.getInventoryApi()).thenReturn(inventory);
		dmo = new DeviceManagedObject(platform);
		 
		createdMo.setId(gid);
		createdMo.setName("createMo");
		createdMo.setSelf("http://self/link/123");
        when(inventory.getManagedObjectApi(any(GId.class))).thenReturn(moHandle);
		when(inventory.create(any(ManagedObjectRepresentation.class))).thenReturn(createdMo);

		when(inventory.update(any(ManagedObjectRepresentation.class))).thenReturn(createdMo);
	}
	
	@Test
	public void create() throws SDKException {
		ManagedObjectRepresentation mo = prepareCreate();
		boolean created = dmo.createOrUpdate(mo, extId, null);

		assertTrue(created);
		verify(inventory).create(mo);
		verify(registry).create(any(ExternalIDRepresentation.class));
	}

	@Test
	public void update() throws SDKException {
		ManagedObjectRepresentation mo = prepareUpdate();
		boolean created = dmo.createOrUpdate(mo, extId, null);
		
		assertFalse(created);
		verify(inventory).update(any(ManagedObjectRepresentation.class)); // Too bad that MORep has no equals method.
	}

	@Test
	public void createIncludingParent() throws SDKException {
		ManagedObjectRepresentation mo = prepareCreate();
		dmo.createOrUpdate(mo, extId, parentId);

		verify(inventory).getManagedObjectApi(parentId);
		verify(moHandle).addChildDevice(any(ManagedObjectReferenceRepresentation.class));
	}

	@Test
	public void updateAddingParent() throws SDKException {
		ManagedObjectRepresentation mo = prepareUpdate();
		dmo.createOrUpdate(mo, extId, parentId);

		verify(inventory).getManagedObjectApi(parentId);
		verify(moHandle).addChildDevice(any(ManagedObjectReferenceRepresentation.class));
	}

	private ManagedObjectRepresentation prepareCreate() throws SDKException {
		when(registry.getExternalId(any(ID.class))).thenThrow(new SDKException(404, "Not registered"));		
		return new ManagedObjectRepresentation();
	}

	private ManagedObjectRepresentation prepareUpdate() throws SDKException {
		ManagedObjectRepresentation mo = new ManagedObjectRepresentation();

		ExternalIDRepresentation eir = new ExternalIDRepresentation();
		mo.setId(gid);
		eir.setManagedObject(mo);
		when(registry.getExternalId(any(ID.class))).thenReturn(eir);
		return mo;
	}
}
