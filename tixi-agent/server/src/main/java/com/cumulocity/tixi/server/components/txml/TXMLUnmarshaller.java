package com.cumulocity.tixi.server.components.txml;

import static com.google.common.cache.CacheBuilder.newBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.cumulocity.tixi.server.model.txml.External;
import com.cumulocity.tixi.server.model.txml.Log;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.services.AgentFileSystem;
import com.google.common.cache.Cache;

@Component
public class TXMLUnmarshaller {

	private final AgentFileSystem agentFileSystem;
	private final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private Unmarshaller unmarshaller;
	private static final Logger log = LoggerFactory.getLogger(TXMLUnmarshaller.class);
	private final Object monitor = new Object();

	private final Cache<Class<?>, Transformer> transformers = newBuilder()
			.expireAfterAccess(1, TimeUnit.HOURS).build();

	@Autowired
	public TXMLUnmarshaller(AgentFileSystem agentFileSystem) {
		this.agentFileSystem = agentFileSystem;
	}
	
	@PostConstruct
	public void init() {
		try {
	        unmarshaller = aJAXBUnmarshaler();
        } catch (JAXBException e) {
        	new RuntimeException("Cant create TXMLUnmarshaller instance!", e);
        }
	}

	@SuppressWarnings("unchecked")
	public <R> R unmarshal(String fileName, Class<R> resultClass) {
		try {
			File incomingFile = agentFileSystem.getIncomingFile(fileName);
			String input = IOUtils.toString(new FileInputStream(incomingFile), "UTF-8");
			input = input.trim();
			if(!StringUtils.hasText(input)) {
				return null;
			}
			input = stripEnclosingBrackets(input);
			
			StreamSource source = new StreamSource(IOUtils.toInputStream(input, "UTF-8"));
			File xsltProcessedFile = agentFileSystem.getXsltProcessedFile(fileName);

			StreamResult transformerResult = new StreamResult(xsltProcessedFile);
			aTransformer(resultClass).transform(source, transformerResult);
			
			synchronized (monitor) {
				StreamSource unmarshallerSource = new StreamSource(xsltProcessedFile);
				return (R) unmarshaller.unmarshal(unmarshallerSource);
            }
		} catch (Exception ex) {
			throw new RuntimeException("Cant unmarshal resource from file " + fileName + " to entity " + resultClass, ex);
		}
	}

	private Transformer aTransformer(final Class<?> clazz) throws Exception {
		return transformers.get(clazz, new Callable<Transformer>() {

			@Override
			public Transformer call() throws Exception {
				File xsltFile = agentFileSystem.getXsltFile(clazz);
				return transformerFactory.newTransformer(new StreamSource(xsltFile));
			}
		});
	}

	private static Unmarshaller aJAXBUnmarshaler() throws JAXBException {
		return JAXBContext.newInstance(LogDefinition.class, Log.class, External.class).createUnmarshaller();
	}
	
	static String stripEnclosingBrackets(String source) {
		if (!source.endsWith("]")) {
			return source;
		}
		log.debug("Remove enclosing brackets");
		source = source.replaceFirst("\\[", "");
		return source.substring(0, source.length() - 1);
	}
}