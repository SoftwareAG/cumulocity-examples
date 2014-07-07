package com.cumulocity.tixi.server.components.txml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.cumulocity.tixi.server.components.txml.MapAdapter.AdaptedMap;
import com.cumulocity.tixi.server.model.txml.logdefinition.LogDefinitionItem;

public class MapAdapter extends XmlAdapter<AdaptedMap, Map<String, LogDefinitionItem>> {

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class AdaptedMap {

		@XmlElements({ @XmlElement(name = "DataloggingItem") })
		private List<LogDefinitionItem> items = new ArrayList<>();

		public List<LogDefinitionItem> getItems() {
			return items;
		}

		public void setItems(List<LogDefinitionItem> items) {
			this.items = items;
		}
	}

	@Override
	public Map<String, LogDefinitionItem> unmarshal(AdaptedMap map) throws Exception {
		Map<String, LogDefinitionItem> result = new HashMap<String, LogDefinitionItem>();
		for (LogDefinitionItem logDefinitionItem : map.getItems()) {
	        result.put(logDefinitionItem.getId(), logDefinitionItem);
        }
		return result;
	}

	@Override
	public AdaptedMap marshal(Map<String, LogDefinitionItem> v) throws Exception {
		throw new UnsupportedOperationException();
	}

}
