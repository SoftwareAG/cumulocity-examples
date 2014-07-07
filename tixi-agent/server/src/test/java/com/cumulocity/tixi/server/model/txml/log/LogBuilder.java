package com.cumulocity.tixi.server.model.txml.log;

import static com.cumulocity.tixi.server.components.txml.TXMLDateAdapter.dateFormatter;

import java.math.BigDecimal;

public class LogBuilder {
	
	private final Log log = new Log();
	private LogItemSet itemSet;
	
	public static LogBuilder aLog() {
		return new LogBuilder();
	}
	
	public LogBuilder withNewItemSet(String id, String dateTime) throws Exception {
		itemSet = new LogItemSet(id, dateFormatter().parse(dateTime));
		log.getItemSets().add(itemSet);
		return this;
	}
	
	public LogBuilder withId(String id) {
		log.setId(id);
		return this;
	}
	
	public LogBuilder withItem(String id, BigDecimal value) {
		LogItem logItem = new LogItem(id, value);
		itemSet.getItems().add(logItem);
		return this;
	}
	
	public Log build() {
		return log;
	}
}
