package com.cumulocity.tixi.server.components.txml;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TXMLValueAdaper extends XmlAdapter<String, BigDecimal> {

	private static final Logger log = LoggerFactory.getLogger(TXMLValueAdaper.class);

	@Override
	public BigDecimal unmarshal(String value) {
		if (value == null) {
			return null;
		}
		try {
			return (BigDecimal) decimalFormatter().parse(value.trim());
		} catch (ParseException pex) {
			log.warn("Cant parse {} to BigDecimal!", value);
			return null;
		}
	}

	@Override
	public String marshal(BigDecimal value) throws Exception {
		return value == null ? null : value.toString();
	}
	
	public static DecimalFormat decimalFormatter() {
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setParseBigDecimal(true);
		return decimalFormat;
	}
}
