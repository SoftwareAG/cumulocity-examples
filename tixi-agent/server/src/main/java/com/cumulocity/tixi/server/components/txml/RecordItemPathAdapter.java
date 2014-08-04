package com.cumulocity.tixi.server.components.txml;

import static jersey.repackaged.com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.tixi.server.model.txml.DeviceVariablePath;
import com.cumulocity.tixi.server.model.txml.ProcessVariablePath;
import com.cumulocity.tixi.server.model.txml.RecordItemPath;
import com.google.common.base.Splitter;

public class RecordItemPathAdapter extends XmlAdapter<String, RecordItemPath> {

	private static final Logger log = LoggerFactory.getLogger(TXMLDateAdapter.class);

	@Override
	public RecordItemPath unmarshal(String value) throws Exception {
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

	private RecordItemPath unmarshalDevicePath(List<String> parts) {
		DeviceVariablePath result = new DeviceVariablePath();
		int index = parts.size() - 1;
		result.setName(parts.get(index--));
		result.setDeviceId(parts.get(index--));
		result.setAgentId(parts.get(index--));
		return result;
	}

	private RecordItemPath unmarshalProcessVariable(List<String> parts) {
		ProcessVariablePath result = new ProcessVariablePath();
		result.setName(parts.get(parts.size() - 1));
		return result;
	}

	@Override
	public String marshal(RecordItemPath value) throws Exception {
		throw new UnsupportedOperationException("Marshaling of " + RecordItemPath.class + " not implemented!");
	}

}
