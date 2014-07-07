package com.cumulocity.tixi.server.components.txml;

import static com.cumulocity.tixi.server.model.txml.log.LogBuilder.aLog;
import static com.cumulocity.tixi.server.model.txml.logdefinition.LogDefinitionBuilder.aLogDefinition;
import static com.cumulocity.tixi.server.model.txml.logdefinition.LogDefinitionItemBuilder.anItem;
import static java.math.BigDecimal.valueOf;
import static org.fest.assertions.Assertions.assertThat;

import java.io.File;

import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

import com.cumulocity.tixi.server.model.txml.log.Log;
import com.cumulocity.tixi.server.model.txml.logdefinition.LogDefinition;

public class TXMLUnmarshallerTest {
	
	TXMLUnmarshaller txmlUnmarshaller = new TXMLUnmarshaller();
	
	@Test
    public void shouldUmnarshalLogDefinitionFile() throws Exception {
		StreamSource streamSource = new StreamSource(new File("src/test/resources/txml/LogDefinition.xml"));
		
		LogDefinition actualLogDefinition = txmlUnmarshaller.unmarshalLogDefinition(streamSource);
				
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
    }
	
	@Test
	public void shouldUmnarshalLogFile() throws Exception {
		StreamSource streamSource = new StreamSource(new File("src/test/resources/txml/Log.xml"));
		
		Log actualLog = txmlUnmarshaller.unmarshalLog(streamSource);
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
