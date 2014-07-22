package com.cumulocity.tixi.server.model.txml;

import static com.cumulocity.tixi.server.components.txml.TXMLDateAdapter.dateFormatter;

import java.math.BigDecimal;
import java.util.Date;

public class LogBuilder {
	
	private final Log log = new Log();
	private Record itemSet;
	
	public static LogBuilder aLog() {
		return new LogBuilder();
	}
	
	public LogBuilder withNewItemSet(String id, String dateTime) throws Exception {
		return withNewItemSet(id, dateFormatter().parse(dateTime));
	}
	
	public LogBuilder withNewItemSet(String id, Date date) throws Exception {
		itemSet = new Record(id, date);
		log.getRecords().add(itemSet);
		return this;
	}
	
	public LogBuilder withId(String id) {
		log.setId(id);
		return this;
	}
	
	public LogBuilder withItem(String id, BigDecimal value) {
		RecordItem logItem = new RecordItem(id, value);
		itemSet.getRecordItems().add(logItem);
		return this;
	}
	
	public Log build() {
		return log;
	}
}
