package com.cumulocity.tixi.server.components.txml;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.cumulocity.tixi.server.model.txml.log.Log;
import com.cumulocity.tixi.server.model.txml.logdefinition.LogDefinition;

public class TXMLUnmarshaller {
	
	private static final String XSLT_HOME = "/META-INF/tixi/txml/";

	public LogDefinition unmarshalLogDefinition(StreamSource source) throws Exception {
		OutputStream transformerOutput = new FileOutputStream("target/tmp.xml");
		InputStream unmarshallerInput = new FileInputStream("target/tmp.xml");
		
		StreamResult transformerResult = new StreamResult(transformerOutput);
		StreamSource unmarshallerSource = new StreamSource(unmarshallerInput);
		Transformer transformer = getTransformer("LogDefinition.xslt");
		transformer.transform(source, transformerResult);
		return unmarshall(unmarshallerSource, LogDefinition.class);
		
	}
	
	public Log unmarshalLog(StreamSource source) throws Exception {
		OutputStream transformerOutput = new FileOutputStream("target/tmp.xml");
		InputStream unmarshallerInput = new FileInputStream("target/tmp.xml");
		
		StreamResult transformerResult = new StreamResult(transformerOutput);
		StreamSource unmarshallerSource = new StreamSource(unmarshallerInput);
		Transformer transformer = getTransformer("Log.xslt");
		transformer.transform(source, transformerResult);
		return null;
		
	}
	
	@SuppressWarnings("unchecked")
	private <R> R unmarshall(StreamSource source, Class<R> resultClazz) {
		try {
			return (R) aJAXBUnmarshaler().unmarshal(source);
		} catch (JAXBException ex) {
			throw new RuntimeException("Cannot parse!", ex);
		}
	}
		
	private Transformer getTransformer(String xsltPath) throws Exception {
		InputStream xsltStream = TXMLUnmarshaller.class.getResourceAsStream(XSLT_HOME + xsltPath);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		StreamSource xslt = new StreamSource(xsltStream);
		return transformerFactory.newTransformer(xslt);
	}
	
	private static Unmarshaller aJAXBUnmarshaler() throws JAXBException {
		return JAXBContext.newInstance(LogDefinition.class).createUnmarshaller();
	}
}
