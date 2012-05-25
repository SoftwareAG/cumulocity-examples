/*
 * Copyright 2012 Nokia Siemens Networks 
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
