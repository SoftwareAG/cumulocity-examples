/*
 * Copyright 2012 Nokia Siemens Networks 
 */
package com.cumulocity.agents.mps.model.measurement.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.svenson.converter.TypeConverter;

public class MpsDateTypeConverter implements TypeConverter {

	public static final String DATE_TIME_FORMAT_PATTERN = "yy-MM-dd HH:mm:ss";
	
	public static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
	
	@Override
	public Object fromJSON(Object in) {
		try {
			return DATE_TIME_FORMAT.parseObject((String) in);
		} catch (Exception e) {
			throw new RuntimeException("Date-time parse error!", e);
		}
	}

	@Override
	public Object toJSON(Object in) {
		return DATE_TIME_FORMAT.format(in);
	}
}
