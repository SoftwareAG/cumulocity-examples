package com.cumulocity.tixi.server.model.txml.log;

public class LogBuilder {
	
	private final Log log = new Log();
	private LogItemSet itemSet;
	
	public static LogBuilder aLog() {
		return new LogBuilder();
	}
	
	public LogBuilder withNewItemSet(String id, String dateTime) {
		itemSet = new LogItemSet(id, dateTime);
		log.getItemSets().add(itemSet);
		return this;
	}
	
	public LogBuilder withId(String id) {
		log.setId(id);
		return this;
	}
	
	public LogBuilder withItem(String id, String value) {
		LogItem logItem = new LogItem(id, value);
		itemSet.getItems().add(logItem);
		return this;
	}

	
	public Log build() {
		return log;
	}

}
