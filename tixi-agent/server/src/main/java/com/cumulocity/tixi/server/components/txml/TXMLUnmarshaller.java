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
		return unmarshal(source, LogDefinition.class);
	}
	
	public Log unmarshalLog(StreamSource source) throws Exception {
		return unmarshal(source, Log.class);
	}
	
	public <R> R unmarshal(StreamSource source, Class<R> resultClass) throws Exception {
		OutputStream transformerOutput = new FileOutputStream("target/tmp.xml");
		InputStream unmarshallerInput = new FileInputStream("target/tmp.xml");
		StreamResult transformerResult = new StreamResult(transformerOutput);
		StreamSource unmarshallerSource = new StreamSource(unmarshallerInput);
		aTransformer(resultClass).transform(source, transformerResult);
		return unmarshall(unmarshallerSource, resultClass);
	}
	
	@SuppressWarnings("unchecked")
	private <R> R unmarshall(StreamSource source, Class<R> resultClazz) {
		try {
			return (R) aJAXBUnmarshaler(resultClazz).unmarshal(source);
		} catch (JAXBException ex) {
			throw new RuntimeException("Cannot parse!", ex);
		}
	}
		
	private Transformer aTransformer(Class<?> clazz) throws Exception {
		InputStream xsltStream = TXMLUnmarshaller.class.getResourceAsStream(XSLT_HOME + clazz.getSimpleName() + ".xslt");
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		StreamSource xslt = new StreamSource(xsltStream);
		return transformerFactory.newTransformer(xslt);
	}
	
	private static Unmarshaller aJAXBUnmarshaler(Class<?> resultClass) throws JAXBException {
		return JAXBContext.newInstance(resultClass).createUnmarshaller();
	}
}
