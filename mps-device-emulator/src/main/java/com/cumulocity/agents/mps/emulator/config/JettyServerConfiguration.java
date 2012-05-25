/*
 * Copyright 2012 Nokia Siemens Networks 
 */
package com.cumulocity.agents.mps.emulator.config;

import java.util.EnumSet;

import org.apache.cxf.Bus;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.DispatcherType;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.filter.RequestContextFilter;

/**
 * The Embedded Jetty server configuration.
 * @author Darek Kaczynski
 */
@Configuration
@ImportResource("classpath:META-INF/cxf/cxf.xml")
public class JettyServerConfiguration {

	@Value("${server.port}")
	private int serverPort;
	
	@Value("${server.max.idle}")
	private int maxIdleTime;
	
	@Value("${server.acceptors}")
	private int acceptors;
	
	@Value("${server.confidential.port}")
	private int confidentialPort;
	
	/**
	 * @return the server thread pool.
	 */
	@Bean
	public ThreadPool threadPool() {
		return new ExecutorThreadPool(0);
	}
	
	/**
	 * @return the server default connector.
	 */
	@Bean
	public Connector defaultConnector() {
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(serverPort);
		connector.setMaxIdleTime(maxIdleTime);
		connector.setAcceptors(acceptors);
		connector.setConfidentialPort(confidentialPort);
		return connector;
	}
	
	/**
	 * Constructs the CXF servlet for servicing REST calls.
	 * @param bus the CXF bus (declared in imported <tt>"classpath:META-INF/cxf/cxf.xml"</tt>).
	 * @return the CXF servlet handler.
	 */
	@Bean
	@Autowired
	public ServletContextHandler cxfServletHandler(Bus bus) {
		CXFNonSpringServlet cxfServlet = new CXFNonSpringServlet();
		cxfServlet.setBus(bus);
		
		ServletHolder holder = new ServletHolder(cxfServlet);
		holder.setName("cxfServlet");
		
		ServletContextHandler handler = new ServletContextHandler();
		handler.addServlet(holder, "/*");
		
		FilterHolder filterHolder = new FilterHolder(new RequestContextFilter());
		handler.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
		
		return handler;
	}
	
	/**
	 * Construct the Embedded Jetty server.
	 * @param threadPool the server thread pool.
	 * @param connectors the server connectors.
	 * @param handlers the server handlers.
	 * @return the Jetty server.
	 */
	@Bean(destroyMethod = "stop")
	@Autowired
	public Server server(ThreadPool threadPool, Connector[] connectors, Handler[] handlers) {
		Server server = new Server();
		server.setThreadPool(threadPool);
		server.setConnectors(connectors);
		HandlerCollection handler = new HandlerCollection();
		handler.setHandlers(handlers);
		server.setHandler(handler);
		return server;
	}
}
