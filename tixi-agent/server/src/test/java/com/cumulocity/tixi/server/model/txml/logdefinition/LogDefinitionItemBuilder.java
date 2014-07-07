package com.cumulocity.tixi.server.model.txml.logdefinition;

public class LogDefinitionItemBuilder {

	private final LogDefinitionItem result = new LogDefinitionItem();

	public static LogDefinitionItemBuilder anItem() {
		return new LogDefinitionItemBuilder();
	}

	public LogDefinitionItemBuilder withName(String name) {
		result.setName(name);
		return this;
	}

	public LogDefinitionItemBuilder withType(String type) {
		result.setType(type);
		return this;
	}

	public LogDefinitionItemBuilder withSize(int size) {
		result.setSize(size);
		return this;
	}

	public LogDefinitionItemBuilder withExp(int exp) {
		result.setExp(exp);
		return this;
	}

	public LogDefinitionItemBuilder withFormat(String format) {
		result.setFormat(format);
		return this;
	}

	public LogDefinitionItemBuilder withId(String id) {
		result.setId(id);
		return this;
	}
	
	public LogDefinitionItemBuilder withPath(String path) {
		result.setPath(path);
		return this;
	}

	public LogDefinitionItem build() {
		return result;
	}
}
