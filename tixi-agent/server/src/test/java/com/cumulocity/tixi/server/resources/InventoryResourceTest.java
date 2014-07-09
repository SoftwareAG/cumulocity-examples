package com.cumulocity.tixi.server.resources;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.cumulocity.tixi.server.model.RequestType;
import com.cumulocity.tixi.server.request.util.Device;

public class InventoryResourceTest {
	
	Device device = mock(Device.class);
	InventoryResource inventoryResource  = new InventoryResource(device);
	ArgumentCaptor<RequestType> reqTypeCaptor = ArgumentCaptor.forClass(RequestType.class);
	
	@Test
    public void shouldRequestXmlFiles() throws Exception {
	    Response response = inventoryResource.open();
	    
	    verify(device, times(2)).put(reqTypeCaptor.capture());
	    
	    assertThat(reqTypeCaptor.getAllValues()).containsExactly(RequestType.EXTERNAL_DATABASE, RequestType.LOG_DEFINITION);
	    assertThat(response.getEntity()).isEqualTo(TixiJsonResponse.statusOKJson());
    }
}
