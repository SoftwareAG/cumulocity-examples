package com.cumulocity.tixi.server.model.txml;

import com.cumulocity.tixi.server.components.txml.RecordItemPathAdapter;
import com.cumulocity.tixi.server.model.txml.RecordItemDefinition;
import com.cumulocity.tixi.server.model.txml.RecordItemPath;

public class RecordItemDefinitionBuilder {

	private final RecordItemDefinition result = new RecordItemDefinition();
	private final RecordItemPathAdapter itemPathAdapter = new RecordItemPathAdapter();

	public static RecordItemDefinitionBuilder anItem() {
		return new RecordItemDefinitionBuilder();
	}

	public RecordItemDefinitionBuilder withName(String name) {
		result.setName(name);
		return this;
	}

	public RecordItemDefinitionBuilder withType(String type) {
		result.setType(type);
		return this;
	}

	public RecordItemDefinitionBuilder withSize(int size) {
		result.setSize(size);
		return this;
	}

	public RecordItemDefinitionBuilder withExp(int exp) {
		result.setExp(exp);
		return this;
	}

	public RecordItemDefinitionBuilder withFormat(String format) {
		result.setFormat(format);
		return this;
	}

	public RecordItemDefinitionBuilder withId(String id) {
		result.setId(id);
		return this;
	}
	
	public RecordItemDefinitionBuilder withPath(RecordItemPath path) {
		result.setPath(path);
		return this;
	}
	
	public RecordItemDefinitionBuilder withPath(String path) throws Exception {
		return withPath(itemPathAdapter.unmarshal(path));
	}

	public RecordItemDefinition build() {
		return result;
	}
}
