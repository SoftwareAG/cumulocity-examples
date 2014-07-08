package com.cumulocity.tixi.server.components.txml;

import static com.google.common.cache.CacheBuilder.newBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.tixi.server.model.txml.Log;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.services.AgentFileSystem;
import com.google.common.cache.Cache;

@Component
public class TXMLUnmarshaller {
    
    private final AgentFileSystem agentFileSystem;
    private final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    
    // @formatter:off
    private final Cache<Class<?>, Transformer> transformers = newBuilder()
    		.expireAfterAccess(1, TimeUnit.HOURS)
    		.build();
    // @formatter:on

    @Autowired
    public TXMLUnmarshaller(AgentFileSystem agentFileSystem) {
    	this.agentFileSystem = agentFileSystem;
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
            return (R) aJAXBUnmarshaler().unmarshal(source);
        } catch (JAXBException ex) {
            throw new RuntimeException("Cannot parse!", ex);
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
        return JAXBContext.newInstance(LogDefinition.class, Log.class).createUnmarshaller();
    }
}