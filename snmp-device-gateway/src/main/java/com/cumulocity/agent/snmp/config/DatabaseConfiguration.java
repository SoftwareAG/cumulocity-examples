package com.cumulocity.agent.snmp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.cumulocity.agent.snmp.repository.DataStore;
import com.cumulocity.agent.snmp.repository.FileDataStore;

@Configuration
public class DatabaseConfiguration {

	@Bean
	@Primary
	public DataStore dataStore() {
		return new FileDataStore();
	}
}