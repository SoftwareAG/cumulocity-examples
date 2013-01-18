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
