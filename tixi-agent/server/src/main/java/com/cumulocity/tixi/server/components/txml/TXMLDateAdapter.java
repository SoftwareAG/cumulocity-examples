package com.cumulocity.tixi.server.components.txml;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TXMLDateAdapter extends XmlAdapter<String, Date> {
	
	private static final Logger log = LoggerFactory.getLogger(TXMLDateAdapter.class);
	
	private static final String DATE_FORMAT = "yyyy/MM/dd,HH:mm:ss";

	@Override
    public Date unmarshal(String dateStr) {
		if(dateStr == null) {
			return null;
		}
		try {
			return dateFormatter().parse(dateStr.trim());
		} catch (ParseException pex) {
			log.warn("Can't parse date {} to format {}, return null.", dateStr, DATE_FORMAT);
			return null;
		}
    }

	@Override
    public String marshal(Date date) throws Exception {
		if(date == null) {
			return null;
		}
	    return dateFormatter().format(date);
    }
	
	public static DateFormat dateFormatter() {
		return new SimpleDateFormat(DATE_FORMAT);
	}
}
