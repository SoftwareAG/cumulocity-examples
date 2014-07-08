package com.cumulocity.tixi.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cumulocity.tixi.server.components.txml.TXMLUnmarshaller;
import com.cumulocity.tixi.server.model.txml.Log;
import com.cumulocity.tixi.server.model.txml.LogDefinition;

@Component
public class TixiXmlHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(TixiXmlHandler.class);
	
	private final TXMLUnmarshaller txmlUnmarshaller;

	public TixiXmlHandler(TXMLUnmarshaller txmlUnmarshaller) {
	    this.txmlUnmarshaller = txmlUnmarshaller;
    }

	public void handle(String fileName, Class<?> entityType) {
		logger.debug("Process fileName " + fileName + " with expected entity " + entityType);		
		Object unmarshaled = null;
		try {
	        unmarshaled = txmlUnmarshaller.unmarshal(fileName, entityType);
        } catch (Exception ex) {
        	throw new RuntimeException("Cant unmarshal resource from file " + fileName + " to entity " + entityType, ex);
        }
		if(unmarshaled instanceof Log) {
			handle((Log) unmarshaled);
		} else if(unmarshaled instanceof LogDefinition) {
			handle((LogDefinition) unmarshaled);
		} else {
			throw new RuntimeException("Can't handle " + unmarshaled);
		}
	}

	private void handle(Log log) {
	    
    }
	
	private void handle(LogDefinition logDefinition) {
		
	}
	
	
	

}
