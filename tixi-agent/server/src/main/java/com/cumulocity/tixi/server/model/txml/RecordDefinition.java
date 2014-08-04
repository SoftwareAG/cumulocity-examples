package com.cumulocity.tixi.server.model.txml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cumulocity.tixi.server.components.txml.TXMLMapAdapter;
import com.cumulocity.tixi.server.components.txml.TXMLMapAdapter.AdaptedMap;

public class RecordDefinition extends LogBaseItem {

	public static class RecordItemDefinitionAdaptedMap implements AdaptedMap<RecordItemDefinition> {

		@XmlElements({ @XmlElement(name = "RecordItemDefinition") })
		private List<RecordItemDefinition> elements;
		
		public List<RecordItemDefinition> getElements() {
			return elements;
		}
	}
	
	public static class RecordItemDefinitionMapAdapter 
		extends TXMLMapAdapter<RecordItemDefinition, RecordItemDefinitionAdaptedMap> {
	}

	@XmlElement(name = "recordItemDefinitions")
	@XmlJavaTypeAdapter(RecordItemDefinitionMapAdapter.class)
	private Map<String, RecordItemDefinition> recordItemDefinitions = new HashMap<>();

	public RecordDefinition() {
	}

	public RecordDefinition(String id) {
		super(id);
	}

	public Map<String, RecordItemDefinition> getRecordItemDefinitions() {
		return recordItemDefinitions;
	}

	public void setRecordItemDefinitions(Map<String, RecordItemDefinition> recordItemDefinitions) {
		this.recordItemDefinitions = recordItemDefinitions;
	}

	@Override
    public String toString() {
	    return String.format("RecordDefinition [recordItemDefinitions=%s, id=%s]", recordItemDefinitions, id);
    }

	public RecordItemDefinition getRecordItemDefinition(String recordItemId) {
		if(recordItemDefinitions == null) {
			return null;
		} else {
			return recordItemDefinitions.get(recordItemId);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((recordItemDefinitions == null) ? 0 : recordItemDefinitions.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecordDefinition other = (RecordDefinition) obj;
		if (recordItemDefinitions == null) {
			if (other.recordItemDefinitions != null)
				return false;
		} else if (!recordItemDefinitions.equals(other.recordItemDefinitions))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
