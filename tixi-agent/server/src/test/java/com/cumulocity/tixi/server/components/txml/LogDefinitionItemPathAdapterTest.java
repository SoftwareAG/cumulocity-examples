package com.cumulocity.tixi.server.components.txml;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.cumulocity.tixi.server.model.txml.RecordItemPath;

public class LogDefinitionItemPathAdapterTest {
	
	RecordItemPathAdapter adapter = new RecordItemPathAdapter();
	
	@Test
    public void shouldCreateItemPath() throws Exception {
		assertThat(adapter.unmarshal("/Process/M-Bus/DHZ-2/DeviceState")).isEqualTo(
				new RecordItemPath("M-Bus", "DHZ-2", "DeviceState"));
		
		assertThat(adapter.unmarshal("/Process/PV/somethink")).isNull();
		assertThat(adapter.unmarshal("...babble....")).isNull();
	    
    }

}
