package com.cumulocity.tixi.server.services;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.tixi.server.request.util.RequestIdFactory;
import com.cumulocity.tixi.server.request.util.RequestStorage;
import com.cumulocity.tixi.server.resources.TixiRequest;

public class TixiRequestFactoryTest {
	
	private final RequestIdFactory requestIdFactory = mock(RequestIdFactory.class);
    private final RequestStorage requestStorage = mock(RequestStorage.class);
    private final TixiRequestFactory tixiRequestFactory = new TixiRequestFactory(requestIdFactory, requestStorage);
    
    @Before
    public void init() {
    	when(requestIdFactory.get()).thenReturn(1L);
    }
    
    @Test
    public void shouldCreateCorrectLogRequest() throws Exception {
    	TixiRequest actual = tixiRequestFactory.createLogRequest("DATALOGING_0");
    	
    	TixiRequest expected = new TixiRequest("TiXML")
    		.set("parameter", "[<ReadLog _=\"DATALOGING_0\" ver=\"v\"/>]")
    		.set("requestId", "1");
    	
    	assertThat(actual).isEqualTo(expected);
    }


}
