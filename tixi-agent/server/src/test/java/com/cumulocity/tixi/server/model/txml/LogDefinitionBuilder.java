package com.cumulocity.tixi.server.model.txml;


public class LogDefinitionBuilder {

	private final LogDefinition result = new LogDefinition();
	private RecordDefinition logDefinitionItemSet;

	public static LogDefinitionBuilder aLogDefinition() {
		return new LogDefinitionBuilder();
	}

	public LogDefinitionBuilder withNewItemSet(String id) {
		logDefinitionItemSet = new RecordDefinition(id);
		result.getRecordDefinitions().put(id, logDefinitionItemSet);
		result.getRecordIds().add(new LogBaseItem(id));
		return this;
	}
	
	public LogDefinitionBuilder withItem(RecordItemDefinition dataLoggingItem) {
		logDefinitionItemSet.getRecordItemDefinitions().put(dataLoggingItem.getId(), dataLoggingItem);
		return this;
	}
	
	public LogDefinitionBuilder withItem(LogDefinitionItemBuilder dataLoggingItem) {
		return withItem(dataLoggingItem.build());
	}

	public LogDefinition build() {
		return result;
	}

}
