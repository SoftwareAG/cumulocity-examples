package com.cumulocity.tixi.server.components.txml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.cumulocity.tixi.server.components.txml.TXMLMapAdapter.AdaptedMap;
import com.cumulocity.tixi.server.model.txml.LogBaseItem;

public class TXMLMapAdapter<E extends LogBaseItem, M extends AdaptedMap<E>> extends XmlAdapter<M, Map<String, E>> {

	public static interface AdaptedMap<K extends LogBaseItem> {
		
		public abstract List<K> getItems();
	}

	@Override
    public Map<String, E> unmarshal(M v) throws Exception {
		Map<String, E> result = new HashMap<>();
		for (E logDefinitionItem : v.getItems()) {
			result.put(logDefinitionItem.getId(), logDefinitionItem);
		}
		return result;
    }

	@Override
    public M marshal(Map<String, E> v) throws Exception {
		throw new UnsupportedOperationException();
    }
}
