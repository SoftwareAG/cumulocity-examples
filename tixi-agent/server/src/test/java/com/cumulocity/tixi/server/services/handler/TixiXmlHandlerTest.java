package com.cumulocity.tixi.server.services.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.ListableBeanFactory;

import com.cumulocity.tixi.server.components.txml.TXMLUnmarshaller;
import com.cumulocity.tixi.server.model.txml.Log;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.services.TixiXmlService;

public class TixiXmlHandlerTest {
	
	private TXMLUnmarshaller unmarshaller = mock(TXMLUnmarshaller.class);
	private ListableBeanFactory beanFactory = mock(ListableBeanFactory.class);
	private TixiLogHandler tixiLogHandler = mock(TixiLogHandler.class);
	private TixiLogDefinitionHandler tixiLogDefinitionHandler = mock(TixiLogDefinitionHandler.class);
	
	private TixiXmlService bean;
	
	@Before
	public void init() {
		bean = new TixiXmlService(unmarshaller, beanFactory);
		when(beanFactory.getBean(TixiLogHandler.class)).thenReturn(tixiLogHandler);
		when(beanFactory.getBean(TixiLogDefinitionHandler.class)).thenReturn(tixiLogDefinitionHandler);
	}
	
	@Test
	public void shouldUnmarshalXmlAndCallCorrectHandlerForLog() throws Exception {
		Log log = new Log();
		when(unmarshaller.unmarshal("anyfile", Log.class)).thenReturn(log);
		
		bean.handleLog("anyfile", "DataLogging_0");
		
		verify(tixiLogHandler).handle(log, "DataLogging_0");
	}
	
	@Test
	public void shouldUnmarshalXmlAndCallCorrectHandlerForLogDefinition() throws Exception {
		LogDefinition logDefinition = new LogDefinition();
		when(unmarshaller.unmarshal("anyfile", LogDefinition.class)).thenReturn(logDefinition);
		
		bean.handleLogDefinition("anyfile");
		
		verify(tixiLogDefinitionHandler).handle(logDefinition);
	}
}
