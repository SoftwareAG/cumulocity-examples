package com.cumulocity.tixi.server.services.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.tixi.server.components.txml.TXMLUnmarshaller;
import com.cumulocity.tixi.server.model.txml.Log;
import com.cumulocity.tixi.server.model.txml.LogDefinition;

@Component
public class TixiXmlService {

	private static final Logger logger = LoggerFactory.getLogger(TixiXmlService.class);

	private final TXMLUnmarshaller txmlUnmarshaller;
	private final ListableBeanFactory beanFactory;

	@Autowired
	public TixiXmlService(TXMLUnmarshaller txmlUnmarshaller, ListableBeanFactory listableBeanFactory) {
	    this.txmlUnmarshaller = txmlUnmarshaller;
		this.beanFactory = listableBeanFactory;
	}

	public void handleLogDefinition(String fileName, Class<?> entityType) {
		logger.info("Process Log Definition file " + fileName + " with expected entity " + entityType.getSimpleName());
		Object unmarshaled = txmlUnmarshaller.unmarshal(fileName, entityType);
		TixiLogDefinitionHandler handler = beanFactory.getBean(TixiLogDefinitionHandler.class);
		handler.handle((LogDefinition) unmarshaled);
		logger.info("File " + fileName + " with expected entity " + entityType.getSimpleName() + " processed.");
	}
	
	public void handleLog(String fileName, String origFileName, Class<?> entityType) {
        logger.info("Process Log file " + fileName + " with expected entity " + entityType.getSimpleName());
        Object unmarshaled = txmlUnmarshaller.unmarshal(fileName, entityType);
        TixiLogHandler handler = beanFactory.getBean(TixiLogHandler.class);
        handler.handle((Log) unmarshaled, origFileName);
        logger.info("File " + fileName + " with expected entity " + entityType.getSimpleName() + " processed.");
    }
	
	public void handleExternal(String fileName, String origFileName, Class<?> entityType) {
        logger.info("Process file " + fileName + " with expected entity " + entityType.getSimpleName());
        logger.info("File " + fileName + " with expected entity " + entityType.getSimpleName() + " processed.");
    }
}
