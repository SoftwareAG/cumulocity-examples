package com.cumulocity.tixi.server.components.txml;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.cumulocity.tixi.server.model.txml.DeviceVariablePath;
import com.cumulocity.tixi.server.model.txml.ProcessVariablePath;

public class LogDefinitionItemPathAdapterTest {
	
	RecordItemPathAdapter adapter = new RecordItemPathAdapter();
	
	@Test
    public void shouldCreateItemPath() throws Exception {
		assertThat(adapter.unmarshal("/Process/M-Bus/DHZ-2/DeviceState")).isEqualTo(
				new DeviceVariablePath("M-Bus", "DHZ-2", "DeviceState"));
		
		assertThat(adapter.unmarshal("/Process/PV/somethink")).isEqualTo(
                new ProcessVariablePath("somethink"));
		
		assertThat(adapter.unmarshal("...babble....")).isNull();
    }

}
