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

package com.cumulocity.agents.mps.emulator;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 * The emulator main class.
 * @author Darek Kaczynski
 */
public class MpsDeviceEmulatorMain implements Runnable {
    
    private static final Logger LOG = LoggerFactory.getLogger(MpsDeviceEmulatorMain.class);

	private volatile AnnotationConfigApplicationContext ctx;
	private volatile Server server;
	
	/**
	 * The emulator main entry method.
	 * @param args arguments.
	 * @throws Exception on execution error.
	 */
	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		new MpsDeviceEmulatorMain().run();
	}
	
	/**
	 * Runs the emulator.
	 * @throws Exception on execution error.
	 */
	public void run() {
	    LOG.info("emulator starting");
	    
		ctx = new AnnotationConfigApplicationContext();
		ctx.scan("com.cumulocity.agents.mps.emulator");
		ctx.addBeanFactoryPostProcessor(serverPropertyPlaceholderConfigurer());
		ctx.refresh();
		ctx.registerShutdownHook();
		
		server = BeanFactoryUtils.beanOfType(ctx, Server.class);
		try {
			server.start();
			server.join();

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			ctx.close();
		}
	}
	
	public AnnotationConfigApplicationContext getCtx() {
		return ctx;
	}
	
	public Server getServer() {
		return server;
	}
	
	/**
	 * Constructs the bean factory properties placeholder configurer. The default properties
	 * can be overriden by system properties.
	 * @return the post-processor.
	 */
	private BeanFactoryPostProcessor serverPropertyPlaceholderConfigurer() {
		PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
		configurer.setLocation(new ClassPathResource("META-INF/mps.device.emulator.default.properties"));
		configurer.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
		return configurer;
	}
}
