package com.cumulocity.tixi.server.resources;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.request.util.RequestStorage;
import com.cumulocity.tixi.server.services.AgentFileSystem;
import com.cumulocity.tixi.server.services.TixiXmlService;

public class SendDataResourceTest {
	
    private final RequestStorage requestStorage = mock(RequestStorage.class); 
    
    private final AgentFileSystem agentFileSystem = mock(AgentFileSystem.class);

    private final TixiXmlService tixiService = mock(TixiXmlService.class);
    
    private final FormDataContentDisposition formDataContentDisposition = mock(FormDataContentDisposition.class);
    
    private SendDataResource bean = new SendDataResource(tixiService, requestStorage, agentFileSystem);
    
    private InputStream inputStream = mock(InputStream.class);
    
    @Test
    @Ignore
    public void shouldHandleTixiRequestWithEntityClass() throws Exception {
    	Mockito.<Class<?>>when(requestStorage.get("requestId")).thenReturn(LogDefinition.class);
    	when(agentFileSystem.writeIncomingFile("testFile", inputStream)).thenReturn("fileName");
    	
	    bean.senddata(inputStream, formDataContentDisposition, "requestId", "some_serial");
	    
	    verify(tixiService).handleLogDefinition("fileName");
    }
    
    @Test
    @Ignore
    public void shouldHandleTixiRequestWithDefaultClass() throws Exception {
    	when(agentFileSystem.writeIncomingFile("testFile", inputStream)).thenReturn("fileName");
    	
    	bean.senddata(inputStream, formDataContentDisposition, null, "some_serial");
    	
    	verify(tixiService).handleLog("fileName", null);
    }
}
