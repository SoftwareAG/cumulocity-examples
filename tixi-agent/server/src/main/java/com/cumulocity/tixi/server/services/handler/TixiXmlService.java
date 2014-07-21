package com.cumulocity.tixi.server.services.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.tixi.server.components.txml.TXMLUnmarshaller;
import com.cumulocity.tixi.server.model.txml.External;
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

	public void handleLogDefinition(String fileName) {
		LogDefinition unmarshaled = parse(fileName, LogDefinition.class);
		getBean(TixiLogDefinitionHandler.class).handle(unmarshaled);
		logger.info("File " + fileName + " with expected entity " + LogDefinition.class.getSimpleName() + " processed.");
	}
	
	public void handleLog(String fileName, String origFileName) {
		Log unmarshaled = parse(fileName, Log.class);
        beanFactory.getBean(TixiLogHandler.class).handle(unmarshaled, origFileName);
        logger.info("File " + fileName + " with expected entity " + Log.class.getSimpleName() + " processed.");
    }
	
	public void handleExternal(String fileName, String origFileName) {
		External unmarshaled = parse(fileName, External.class);
		beanFactory.getBean(TixiExternalHandler.class).handle(unmarshaled, origFileName);
        logger.info("File " + fileName + " with expected entity " + External.class.getSimpleName() + " processed.");
    }
	
	private <S> S parse(String fileName, Class<S> expectedType) {
		logger.info("Process " + fileName + " with expected entity " + expectedType.getSimpleName());
		return txmlUnmarshaller.unmarshal(fileName, expectedType);
	}
	
	private <T> T getBean(Class<T> clazz) {
		return beanFactory.getBean(clazz);
	}
}
