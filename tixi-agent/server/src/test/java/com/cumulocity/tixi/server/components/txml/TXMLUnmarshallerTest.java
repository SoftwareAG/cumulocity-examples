package com.cumulocity.tixi.server.components.txml;

import static com.cumulocity.tixi.server.model.txml.LogBuilder.aLog;
import static com.cumulocity.tixi.server.model.txml.LogDefinitionBuilder.aLogDefinition;
import static com.cumulocity.tixi.server.model.txml.RecordItemDefinitionBuilder.anItem;
import static java.math.BigDecimal.valueOf;
import static org.fest.assertions.Assertions.assertThat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.tixi.server.model.txml.External;
import com.cumulocity.tixi.server.model.txml.External.Bus;
import com.cumulocity.tixi.server.model.txml.External.Device;
import com.cumulocity.tixi.server.model.txml.External.Meter;
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
		agentFileSystem = new AgentFileSystem("target/incoming", "target/xsltprocessed", XSLT_DIR);
		agentFileSystem.init();
		txmlUnmarshaller = new TXMLUnmarshaller(agentFileSystem);
		txmlUnmarshaller.init();
	}
	
	@Test
    public void shouldUmnarshalLogDefinitionFile() throws Exception {
		
		String fileName = agentFileSystem.writeIncomingFile("testFile", new FileInputStream(SAMPLE_DIR + "LogDefinition.xml"));
		
		LogDefinition actualLogDefinition = txmlUnmarshaller.unmarshal(fileName, LogDefinition.class);
		
		System.out.println(actualLogDefinition.getRecordIds());
		
		// @formatter:off
		LogDefinition expectedLogDefinition = aLogDefinition()
			.withNewRecordDef("Datalogging_1")
				.withRecordItemDef(anItem()
					.withId("Item_1")
					.withType("type1")
					.withName("aName1")
					.withPath("/Process/M-Bus/Device-1/Temperature")
					.withSize(11)
					.withExp(12)
					.withFormat("format1"))
			.withNewRecordDef("Datalogging_2")
				.withRecordItemDef(anItem()
					.withId("Item_2")
					.withType("type2")
					.withName("aName2")
					.withPath("/Process/M-Bus/Device-2/Temperature")
					.withSize(21)
					.withExp(22)
					.withFormat("format2"))
				.withRecordItemDef(anItem()
                    .withId("Item_3")
                    .withType("float")
                    .withName("Temp PT1000 1")
                    .withPath("/Process/PV/PT1000_1"))
			.build();
		// @formatter:on
		
		assertThat(actualLogDefinition).isEqualTo(expectedLogDefinition);
	}
		
	@Test
	public void shouldUmnarshalLogFile() throws Exception {
		
		String fileName = agentFileSystem.writeIncomingFile("testFile", new FileInputStream(SAMPLE_DIR + "Log.xml"));
		
		Log actualLog = txmlUnmarshaller.unmarshal(fileName, Log.class);
		// @formatter:off
		Log expectedLog = aLog()
				.withId("Datalogging_1")
				.withNewRecordItemSet("ID_1", "2014/07/07,12:00:00")
					.withRecordItem("Item_1", valueOf(11))
					.withRecordItem("Item_2", valueOf(12))
				.withNewRecordItemSet("ID_2", "2014/07/07,12:15:00")
					.withRecordItem("Item_1", valueOf(21))
					.withRecordItem("Item_2", valueOf(22))
				.build();
		// @formatter:on
		
		assertThat(actualLog).isEqualTo(expectedLog);
	}
	
	@Test
	public void shouldUmnarshalLogDefinitionWithBrackets() throws Exception {
		
		String fileName = agentFileSystem.writeIncomingFile("testFile", new FileInputStream(SAMPLE_DIR + "LogDefinition_withbrackets.xml"));
		
		txmlUnmarshaller.unmarshal(fileName, LogDefinition.class);
	}
		
	@Test
    public void shouldStripEnclosingBrackets() throws Exception {
	    String result = TXMLUnmarshaller.stripEnclosingBrackets("<xml>[content[]content] ");
	    
	    assertThat(result).isEqualTo("<xml>content[]content");
    }
	
	@Test
	public void shouldUmnarshalNewLogDefinitionFile() throws Exception {
		
		String fileName = agentFileSystem.writeIncomingFile("testFile", new FileInputStream(SAMPLE_DIR + "LogDefinition_10_20140717132044325.xml"));
		
		txmlUnmarshaller.unmarshal(fileName, LogDefinition.class);
	}
	
	@Test
	public void shouldUmnarshalNewLogFile() throws Exception {
		
		String fileName = writeIncomingFile("Log_1.xml", "testFile");
		
		txmlUnmarshaller.unmarshal(fileName, Log.class);
	}
	
	@Test
	public void shouldUmnarshalNewExternalFile() throws Exception {
		shouldUmnarshalNewExternalFile("External_1.xml");
	}
	
	@Test
	public void shouldUmnarshalNewExternalFileWithNoGetConfigTag() throws Exception {
		shouldUmnarshalNewExternalFile("External_2.xml");
	}

	private void shouldUmnarshalNewExternalFile(String sourceFileName) throws FileNotFoundException {
	    String fileName = writeIncomingFile(sourceFileName, sourceFileName);
		
		External external = txmlUnmarshaller.unmarshal(fileName, External.class);
		
		assertThat(external.getBuses()).containsExactly(new Bus("Bus_0"), new Bus("Bus_1"));
		assertThat(external.getBuses().get(0).getDevices()).containsExactly(new Device("Device_00"));
		assertThat(external.getBuses().get(0).getDevices().get(0).getMeters()).containsExactly(
				new Meter("Temperatur_U1"), new Meter("Temperatur_U2"));
		assertThat(external.getBuses().get(1).getDevices()).containsExactly(
				new Device("Device_10"), new Device("Device_11"));
		assertThat(external.getBuses().get(1).getDevices().get(0).getMeters()).containsExactly(new Meter("Energy_100"));
		assertThat(external.getBuses().get(1).getDevices().get(1).getMeters()).containsExactly(new Meter("Energy_110"));
    }

	private String writeIncomingFile(String sourceFileName, String targetFileNamePrefix) throws FileNotFoundException {
	    return agentFileSystem.writeIncomingFile(targetFileNamePrefix, new FileInputStream(SAMPLE_DIR + sourceFileName));
    }
}

