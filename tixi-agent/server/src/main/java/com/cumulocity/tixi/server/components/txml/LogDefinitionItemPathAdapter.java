package com.cumulocity.tixi.server.components.txml;

import static jersey.repackaged.com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.tixi.server.model.txml.logdefinition.LogDefinitionItemPath;
import com.google.common.base.Splitter;

public class LogDefinitionItemPathAdapter extends XmlAdapter<String, LogDefinitionItemPath> {

	private static final Logger log = LoggerFactory.getLogger(TXMLDateAdapter.class);

	@Override
	public LogDefinitionItemPath unmarshal(String value) throws Exception {
		List<String> parts = newArrayList(Splitter.on("/").omitEmptyStrings().split(value));
		if (isDevicePath(parts)) {
			return unmarshalDevicePath(parts);
		} else if (isProcessVariablePath(parts)) {
			return unmarshalProcessVariable(parts);
		}
		log.warn("Can't parse path {}!", value);
		return null;
	}

	private boolean isDevicePath(List<String> parts) {
		return parts.size() == 4;
	}

	private boolean isProcessVariablePath(List<String> parts) {
		return parts.size() == 3 && "PV".equals(parts.get(1));
	}

	private LogDefinitionItemPath unmarshalDevicePath(List<String> parts) {
		LogDefinitionItemPath result = new LogDefinitionItemPath();
		int index = parts.size() - 1;
		result.setName(parts.get(index--));
		result.setDeviceId(parts.get(index--));
		result.setAgentId(parts.get(index--));
		return result;
	}

	private LogDefinitionItemPath unmarshalProcessVariable(List<String> parts) {
		// skip now
		return null;
	}

	@Override
	public String marshal(LogDefinitionItemPath value) throws Exception {
		throw new UnsupportedOperationException("Marshaling of " + LogDefinitionItemPath.class + " not implemented!");
	}

}
