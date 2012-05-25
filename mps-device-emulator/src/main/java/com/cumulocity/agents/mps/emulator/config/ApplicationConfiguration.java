/*
 * Copyright 2012 Nokia Siemens Networks 
 */
package com.cumulocity.agents.mps.emulator.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.spring.JAXRSServerFactoryBeanDefinitionParser.SpringJAXRSServerFactoryBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The application configuration.
 * @author Darek Kaczynski
 */
@Configuration
public class ApplicationConfiguration {

	@Autowired
	private ListableBeanFactory beanFactory;
	
	/**
	 * Creates the JAX-RS server factory. The service beans are auto-discovered using {@link JAXRSResource}
	 * annotation.
	 * @return the JAX-RS server factory.
	 */
	@Bean(initMethod = "create")
	public SpringJAXRSServerFactoryBean springJAXRSServerFactoryBean() {
		SpringJAXRSServerFactoryBean bean = new SpringJAXRSServerFactoryBean();
		bean.setAddress("/");
		bean.setServiceBeans(findServiceBeans());
		return bean;
	}
	
	/**
	 * @return a list of all service beans for JAX-RS server.
	 */
	private List<Object> findServiceBeans() {
		return new ArrayList<Object>(beanFactory.getBeansWithAnnotation(JAXRSResource.class).values());
	}
}
