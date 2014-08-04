package com.cumulocity.tixi.server.model.txml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cumulocity.tixi.server.components.txml.TXMLMapAdapter;
import com.cumulocity.tixi.server.components.txml.TXMLMapAdapter.AdaptedMap;

@XmlRootElement(name = "LogDefinition")
public class LogDefinition {
	
	public static class RecordDefinitionAdaptedMap implements AdaptedMap<RecordDefinition> {

		@XmlElements({ @XmlElement(name = "RecordDefinition") })
		private List<RecordDefinition> elements;
		
		public List<RecordDefinition> getElements() {
			return elements;
		}
	}

	public static class RecordDefinitionMapAdapter 
		extends TXMLMapAdapter<RecordDefinition, RecordDefinitionAdaptedMap> {
	}
	
	@XmlElementWrapper(name = "RecordIds")
	@XmlElement(name = "RecordId")
	private List<LogBaseItem> recordIds = new ArrayList<>();

	@XmlElement(name = "RecordDefinitions")
	@XmlJavaTypeAdapter(RecordDefinitionMapAdapter.class)
	private Map<String, RecordDefinition> recordDefinitions = new HashMap<>();

	public Map<String, RecordDefinition> getRecordDefinitions() {
		return recordDefinitions;
	}

	public void setRecordDefinitions(Map<String, RecordDefinition> recordDefinitions) {
		this.recordDefinitions = recordDefinitions;
	}
	
	public List<LogBaseItem> getRecordIds() {
		return recordIds;
	}

	public void setRecordIds(List<LogBaseItem> recordIds) {
		this.recordIds = recordIds;
	}

	public RecordDefinition getRecordDefinition(String recordId) {
		if(recordDefinitions == null) {
			return null;
		} else {
			return recordDefinitions.get(recordId);
		}
	}
	
	public RecordItemDefinition getItem(String recordId, String recordItemId) {
		RecordDefinition recordDefinition = getRecordDefinition(recordId);
		if(recordDefinition == null) {
			return null;
		} else {
			return recordDefinition.getRecordItemDefinition(recordItemId);
		}
	}
	
	@Override
    public String toString() {
	    return String.format("LogDefinition [recordIds=%s, recordDefinitions=%s]", recordIds, recordDefinitions);
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((recordDefinitions == null) ? 0 : recordDefinitions.hashCode());
	    result = prime * result + ((recordIds == null) ? 0 : recordIds.hashCode());
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
	    LogDefinition other = (LogDefinition) obj;
	    if (recordDefinitions == null) {
		    if (other.recordDefinitions != null)
			    return false;
	    } else if (!recordDefinitions.equals(other.recordDefinitions))
		    return false;
	    if (recordIds == null) {
		    if (other.recordIds != null)
			    return false;
	    } else if (!recordIds.equals(other.recordIds))
		    return false;
	    return true;
    }
}
