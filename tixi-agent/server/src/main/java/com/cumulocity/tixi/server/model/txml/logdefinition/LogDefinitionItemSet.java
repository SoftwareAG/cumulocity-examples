package com.cumulocity.tixi.server.model.txml.logdefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cumulocity.tixi.server.components.txml.TXMLMapAdapter;
import com.cumulocity.tixi.server.components.txml.TXMLMapAdapter.AdaptedMap;
import com.cumulocity.tixi.server.model.txml.HasId;

@XmlRootElement(name = "DataLogging")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogDefinitionItemSet implements HasId {

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class LogDefinitionItemAdaptedMap implements AdaptedMap<LogDefinitionItem> {

		@XmlElements({ @XmlElement(name = "DataloggingItem") })
		private List<LogDefinitionItem> items;
		
		public List<LogDefinitionItem> getItems() {
			return items;
		}
	}
	
	public static class LogDefinitionItemMapAdapter 
		extends TXMLMapAdapter<LogDefinitionItem, LogDefinitionItemAdaptedMap> {
	}

	@XmlAttribute
	private String id;

	@XmlJavaTypeAdapter(LogDefinitionItemMapAdapter.class)
	@XmlElement(name = "DataloggingItems")
	private Map<String, LogDefinitionItem> items = new HashMap<>();

	public LogDefinitionItemSet() {
	}

	public LogDefinitionItemSet(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, LogDefinitionItem> getItems() {
		return items;
	}

	public void setItems(Map<String, LogDefinitionItem> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return String.format("DataLogging [id=%s, items=%s]", id, items);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		LogDefinitionItemSet other = (LogDefinitionItemSet) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
