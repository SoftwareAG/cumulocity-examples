package com.cumulocity.tixi.server.model.txml;

import java.util.concurrent.atomic.AtomicLong;


public class LogDefinitionBuilder {
	
	private static final AtomicLong seq = new AtomicLong();
	private final LogDefinition result = new LogDefinition();
	private RecordDefinition recordDefinition;

	public static LogDefinitionBuilder aLogDefinition() {
		return new LogDefinitionBuilder();
	}

	public LogDefinitionBuilder withNewRecordDef() {
		return withNewRecordDef("record_" + seq.getAndIncrement());
	}
	
	public LogDefinitionBuilder withNewRecordDef(String id) {
		recordDefinition = new RecordDefinition(id);
		result.getRecordDefinitions().put(id, recordDefinition);
		result.getRecordIds().add(new LogBaseItem(id));
		return this;
	}
	
	public LogDefinitionBuilder withRecordItemDef(RecordItemDefinition recordItemDef) {
		recordDefinition.getRecordItemDefinitions().put(recordItemDef.getId(), recordItemDef);
		return this;
	}
	
	public LogDefinitionBuilder withRecordItemDef(RecordItemDefinitionBuilder recordItemDefBuilder) {
		return withRecordItemDef(recordItemDefBuilder.build());
	}

	public LogDefinition build() {
		return result;
	}

}
