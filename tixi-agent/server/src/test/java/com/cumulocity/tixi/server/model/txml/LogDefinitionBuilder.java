package com.cumulocity.tixi.server.model.txml;


public class LogDefinitionBuilder {

	private final LogDefinition result = new LogDefinition();
	private RecordDefinition recordDefinition;

	public static LogDefinitionBuilder aLogDefinition() {
		return new LogDefinitionBuilder();
	}

	public LogDefinitionBuilder withNewRecordDefinition(String id) {
		recordDefinition = new RecordDefinition(id);
		result.getRecordDefinitions().put(id, recordDefinition);
		result.getRecordIds().add(new LogBaseItem(id));
		return this;
	}
	
	public LogDefinitionBuilder withRecordItemDefinition(RecordItemDefinition recordItemDefinition) {
		recordDefinition.getRecordItemDefinitions().put(recordItemDefinition.getId(), recordItemDefinition);
		return this;
	}
	
	public LogDefinitionBuilder withRecordItemDefinition(RecordItemDefinitionBuilder dataLoggingItem) {
		return withRecordItemDefinition(dataLoggingItem.build());
	}

	public LogDefinition build() {
		return result;
	}

}
