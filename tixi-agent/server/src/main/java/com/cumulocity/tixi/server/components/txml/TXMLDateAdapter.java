package com.cumulocity.tixi.server.components.txml;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TXMLDateAdapter extends XmlAdapter<String, Date> {
	
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss");

	@Override
    public Date unmarshal(String dateStr) throws Exception {
		if(dateStr == null) {
			return null;
		}
		dateStr = dateStr.trim();
		return DATE_FORMAT.parse(dateStr);
    }

	@Override
    public String marshal(Date date) throws Exception {
		if(date == null) {
			return null;
		}
	    return DATE_FORMAT.format(date);
    }
}
