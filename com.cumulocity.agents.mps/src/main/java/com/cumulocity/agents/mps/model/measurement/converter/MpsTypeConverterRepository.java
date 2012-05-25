/*
 * Copyright 2012 Nokia Siemens Networks 
 */
package com.cumulocity.agents.mps.model.measurement.converter;

import org.svenson.converter.DefaultTypeConverterRepository;
import org.svenson.converter.TypeConverterRepository;

public class MpsTypeConverterRepository extends DefaultTypeConverterRepository {

	private static final TypeConverterRepository INSTANCE = new MpsTypeConverterRepository();
	
	public static TypeConverterRepository getInstance() {
		return INSTANCE;
	}
	
	public MpsTypeConverterRepository() {
		super();
		addTypeConverter(new MpsDateTypeConverter());
	}
}
