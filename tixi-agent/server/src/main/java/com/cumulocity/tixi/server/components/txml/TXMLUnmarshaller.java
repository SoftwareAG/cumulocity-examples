package com.cumulocity.tixi.server.components.txml;

import java.io.File;
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

import com.cumulocity.tixi.server.model.txml.logdefinition.LogDefinition;

public class TXMLUnmarshaller {
	
	@SuppressWarnings("unchecked")
	@Deprecated
	public <R> R unmarshall(File xmlFile, Class<R> resultClazz) {
		R result = null;
		try {
			Unmarshaller unmarshaller = createJAXBUnmarshaler(resultClazz);
			return (R) unmarshaller.unmarshal(xmlFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public LogDefinition unmarshalLogDefinition(StreamSource source) throws Exception {
		OutputStream transformerOutput = new FileOutputStream("target/tmp.xml");
		InputStream unmarshallerInput = new FileInputStream("target/tmp.xml");
		
		StreamResult transformerResult = new StreamResult(transformerOutput);
		StreamSource unmarshallerSource = new StreamSource(unmarshallerInput);
		transform("src/test/resources/txml/LogDefinition.xslt", source, transformerResult);
		return unmarshall(unmarshallerSource, LogDefinition.class);
		
	}
	
	@SuppressWarnings("unchecked")
	public <R> R unmarshall(StreamSource source, Class<R> resultClazz) {
		R result = null;
		try {
			Unmarshaller unmarshaller = createJAXBUnmarshaler(resultClazz);
			return (R) unmarshaller.unmarshal(source);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void transform(String xsltPath, StreamSource source, StreamResult result) throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();
		StreamSource xslt = new StreamSource(xsltPath);
		Transformer transformer = tf.newTransformer(xslt);
		transformer.transform(source, result);
	}
	
	private static <R> Unmarshaller createJAXBUnmarshaler(Class<R> resultClazz) throws JAXBException {
		return JAXBContext.newInstance(
				LogDefinition.class
				).createUnmarshaller();
	}


}
