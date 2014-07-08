package com.cumulocity.agent.server.jaxrs;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.inject.Inject;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartResolver;

import com.cumulocity.agent.server.Server;
import com.cumulocity.agent.server.context.ContextFilter;
import com.cumulocity.tixi.server.resources.SendDataResource;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.Service;

@Component("jaxrsServer")
public class JaxrsServer implements Server {

    private final String host;

    private final int port;

    private final String applicationId;

    private final ResourceConfig resourceConfig;

    private final WebApplicationContext applicationContext;

    private final Service service = new AbstractService() {

    private HttpServer server;

        @Override
        protected void doStart() {
            server = HttpServer.createSimpleServer(null, new InetSocketAddress(host, port));
            WebappContext context = new WebappContext(applicationId, "/" + applicationId);
            resourceConfig
            .register(RequestContextFilter.class)
            .register(MultiPartFeature.class);
            System.out.println(resourceConfig.getClasses());
            context.addServlet("jersey-servlet", new ServletContainer(resourceConfig)).addMapping("/*");
            context.addFilter("deviceContextFilter", applicationContext.getBean(ContextFilter.class)).addMappingForServletNames(null,
                    "jersey-servlet");
            context.addListener(new ContextLoaderListener(applicationContext));
            context.deploy(server);
            try {
                server.start();
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }

        @Override
        protected void doStop() {
            server.shutdownNow();
        }
    };

    @Autowired
    public JaxrsServer(@Value("${server.host:0.0.0.0}") String host, @Value("${server.port:80}") int port,
            @Value("${server.id}") String contextPath, ResourceConfig resourceConfig, WebApplicationContext context) {
        this.host = host;
        this.port = port;
        this.applicationId = contextPath;
        this.resourceConfig = resourceConfig;
        this.applicationContext = context;
    }

    @Override
    public void start() {
        service.startAsync();
        service.awaitRunning();
    }

    @Override
    public void stop() {
        service.stopAsync();
        service.awaitTerminated();
    }
}
