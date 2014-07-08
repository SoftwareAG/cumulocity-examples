package com.cumulocity.tixi.server.components.txml;

import static com.cumulocity.tixi.server.model.txml.LogBuilder.aLog;
import static com.cumulocity.tixi.server.model.txml.LogDefinitionBuilder.aLogDefinition;
import static com.cumulocity.tixi.server.model.txml.LogDefinitionItemBuilder.anItem;
import static java.math.BigDecimal.valueOf;
import static org.fest.assertions.Assertions.assertThat;

import java.io.File;

import javax.xml.transform.stream.StreamSource;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.tixi.server.model.txml.Log;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.services.AgentFileSystem;

public class TXMLUnmarshallerTest {
	
	AgentFileSystem agentFileSystem;
	TXMLUnmarshaller txmlUnmarshaller;
	
	private static final String SAMPLE_DIR = "src/test/resources/txml/sample/";
	private static final String XSLT_DIR = "src/test/resources/txml/xslt/";
	
	@Before
	public void init() {
		agentFileSystem = new AgentFileSystem("sth", "sth", XSLT_DIR);
		txmlUnmarshaller = new TXMLUnmarshaller(agentFileSystem);
	}
	
	@Test
    public void shouldUmnarshalLogDefinitionFile() throws Exception {
		StreamSource streamSource = new StreamSource(new File(SAMPLE_DIR + "LogDefinition.xml"));
		
		LogDefinition actualLogDefinition = txmlUnmarshaller.unmarshal(streamSource, LogDefinition.class);
				
		// @formatter:off
		LogDefinition expectedLogDefinition = aLogDefinition()
			.withNewItemSet("Datalogging_1")
				.withItem(anItem()
					.withId("Item_1")
					.withType("type1")
					.withName("aName1")
					.withPath("/Process/M-Bus/Device-1/Temperature")
					.withSize(11)
					.withExp(12)
					.withFormat("format1"))
			.withNewItemSet("Datalogging_2")
				.withItem(anItem()
					.withId("Item_2")
					.withType("type2")
					.withName("aName2")
					.withPath("/Process/M-Bus/Device-2/Temperature")
					.withSize(21)
					.withExp(22)
					.withFormat("format2"))
			.build();
		// @formatter:on
		
		assertThat(actualLogDefinition).isEqualTo(expectedLogDefinition);
		
		System.out.println(actualLogDefinition);
    }
	
	@Test
	public void shouldUmnarshalLogFile() throws Exception {
		StreamSource streamSource = new StreamSource(new File(SAMPLE_DIR + "Log.xml"));
		
		Log actualLog = txmlUnmarshaller.unmarshal(streamSource, Log.class);
		// @formatter:off
		Log expectedLog = aLog()
				.withId("Datalogging_1")
				.withNewItemSet("ID_1", "2014/07/07,12:00:00")
					.withItem("Item_1", valueOf(11))
					.withItem("Item_2", valueOf(12))
				.withNewItemSet("ID_2", "2014/07/07,12:15:00")
					.withItem("Item_1", valueOf(21))
					.withItem("Item_2", valueOf(22))
				.build();
		// @formatter:on
		
		assertThat(actualLog).isEqualTo(expectedLog);
	}
}
