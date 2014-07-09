package com.cumulocity.tixi.server.resources;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.junit.Test;
import org.mockito.Mockito;

import com.cumulocity.tixi.server.model.txml.Log;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.request.util.RequestStorage;
import com.cumulocity.tixi.server.services.AgentFileSystem;
import com.cumulocity.tixi.server.services.handler.TixiXmlService;

public class SendDataResourceTest {
	
    private final RequestStorage requestStorage = mock(RequestStorage.class); 
    
    private final AgentFileSystem agentFileSystem = mock(AgentFileSystem.class);

    private final TixiXmlService tixiService = mock(TixiXmlService.class);
    
    private SendDataResource bean = new SendDataResource(tixiService, requestStorage, agentFileSystem);
    
    private InputStream inputStream = mock(InputStream.class);
    
    @Test
    public void shouldHandleTixiRequestWithEntityClass() throws Exception {
    	Mockito.<Class<?>>when(requestStorage.get("requestId")).thenReturn(LogDefinition.class);
    	when(agentFileSystem.writeIncomingFile("requestId", inputStream)).thenReturn("fileName");
    	
	    bean.senddata(inputStream, null, "requestId");
	    
	    verify(tixiService).handle("fileName", LogDefinition.class);
    }
    
    @Test
    public void shouldHandleTixiRequestWithDefaultClass() throws Exception {
    	when(agentFileSystem.writeIncomingFile(null, inputStream)).thenReturn("fileName");
    	
    	bean.senddata(inputStream, null, null);
    	
    	verify(tixiService).handle("fileName", Log.class);
    }
}
