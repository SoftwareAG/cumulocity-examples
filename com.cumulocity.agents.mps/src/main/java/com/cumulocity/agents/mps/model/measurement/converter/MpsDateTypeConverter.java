/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
