package com.cumulocity.tixi.server.model.txml.logdefinition;

public class DataLoggingItemBuilder {

	private DataLoggingItem dataLoggingItem = new DataLoggingItem();

	public static DataLoggingItemBuilder anItem() {
		return new DataLoggingItemBuilder();
	}

	public DataLoggingItemBuilder withName(String name) {
		dataLoggingItem.setName(name);
		return this;
	}

	public DataLoggingItemBuilder withType(String type) {
		dataLoggingItem.setType(type);
		return this;
	}

	public DataLoggingItemBuilder withSize(int size) {
		dataLoggingItem.setSize(size);
		return this;
	}

	public DataLoggingItemBuilder withExp(int exp) {
		dataLoggingItem.setExp(exp);
		return this;
	}

	public DataLoggingItemBuilder withFormat(String format) {
		dataLoggingItem.setFormat(format);
		return this;
	}

	public DataLoggingItemBuilder withLoggingItemName(String loggingItemName) {
		dataLoggingItem.setLoggingItemName(loggingItemName);
		return this;
	}
	
	public DataLoggingItemBuilder withPath(String path) {
		dataLoggingItem.setPath(path);
		return this;
	}

	public DataLoggingItem build() {
		return dataLoggingItem;
	}
}
