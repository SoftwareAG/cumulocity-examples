package com.cumulocity.tixi.server.model.txml.logdefinition;

public class LogDefinitionBuilder {

	private LogDefinition logDefinition = new LogDefinition();
	private DataLogging dataLogging;

	public static LogDefinitionBuilder aLogDefinition() {
		return new LogDefinitionBuilder();
	}

	public LogDefinitionBuilder withNewDatalogging(String tagName) {
		dataLogging = new DataLogging();
		dataLogging.setTagName(tagName);
		logDefinition.getDataLoggings().add(dataLogging);
		return this;
	}

	public LogDefinitionBuilder withDataloggingItem(DataLoggingItem dataLoggingItem) {
		dataLogging.getItems().add(dataLoggingItem);
		return this;
	}
	
	public LogDefinitionBuilder withDataloggingItem(DataLoggingItemBuilder dataLoggingItem) {
		return withDataloggingItem(dataLoggingItem.build());
	}

	public LogDefinition build() {
		return logDefinition;
	}

}
