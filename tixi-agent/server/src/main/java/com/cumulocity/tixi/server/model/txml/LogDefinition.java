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
	
	public static class LogDefinitionItemSetAdaptedMap implements AdaptedMap<LogDefinitionItemSet> {

		@XmlElements({ @XmlElement(name = "LogDefinitionItemSet") })
		private List<LogDefinitionItemSet> items;
		
		public List<LogDefinitionItemSet> getItems() {
			return items;
		}
	}

	public static class LogDefinitionItemSetMapAdapter 
		extends TXMLMapAdapter<LogDefinitionItemSet, LogDefinitionItemSetAdaptedMap> {
	}
	
	@XmlElementWrapper(name = "RecordDefs")
	@XmlElement(name = "RecordDef")
	private List<LogBaseItem> recordDefs = new ArrayList<>();

	@XmlElement(name = "Records")
	@XmlJavaTypeAdapter(LogDefinitionItemSetMapAdapter.class)
	private Map<String, LogDefinitionItemSet> itemSets = new HashMap<>();

	public Map<String, LogDefinitionItemSet> getItemSets() {
		return itemSets;
	}

	public void setItemSets(Map<String, LogDefinitionItemSet> dataLoggings) {
		this.itemSets = dataLoggings;
	}
	
	public List<LogBaseItem> getRecordDefs() {
		return recordDefs;
	}

	public void setRecordDefs(List<LogBaseItem> recordDefs) {
		this.recordDefs = recordDefs;
	}

	public LogDefinitionItemSet getItemSet(String id) {
		if(itemSets == null) {
			return null;
		} else {
			return itemSets.get(id);
		}
	}
	
	public LogDefinitionItem getItem(String itemSetId, String itemId) {
		LogDefinitionItemSet itemSet = getItemSet(itemSetId);
		if(itemSet == null) {
			return null;
		} else {
			return itemSet.getItem(itemId);
		}
	}

	@Override
    public String toString() {
	    return String.format("LogDefinition [recordDefs=%s, itemSets=%s]", recordDefs, itemSets);
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((itemSets == null) ? 0 : itemSets.hashCode());
	    result = prime * result + ((recordDefs == null) ? 0 : recordDefs.hashCode());
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
	    if (itemSets == null) {
		    if (other.itemSets != null)
			    return false;
	    } else if (!itemSets.equals(other.itemSets))
		    return false;
	    if (recordDefs == null) {
		    if (other.recordDefs != null)
			    return false;
	    } else if (!recordDefs.equals(other.recordDefs))
		    return false;
	    return true;
    }
}
