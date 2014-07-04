package com.cumulocity.agent.server;

import java.net.InetSocketAddress;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import com.cumulocity.agent.server.config.PropertiesFactoryBean;
import com.cumulocity.agent.server.config.ServerConfiguration;
import com.cumulocity.agent.server.protocol.ProtocolDecoder;
import com.cumulocity.agent.server.protocol.ProtocolEncoder;

public class ServerBuilder {

    public static interface ApplicationBuilder {
        public ServerBuilder application(String id);
    }

    private final InetSocketAddress address;

    private final String applicationId;

    private final Set<String> configurations = new LinkedHashSet<String>();

    protected InetSocketAddress address() {
        return address;
    }

    public static ApplicationBuilder on(final InetSocketAddress address) {
        return new ApplicationBuilder() {

            @Override
            public ServerBuilder application(String id) {
                return new ServerBuilder(address, id);
            }
        };
    }

    public static ApplicationBuilder on(final int port) {
        return on(new InetSocketAddress(port));
    }

    private ServerBuilder(InetSocketAddress address, String id) {
        this.address = address;
        this.applicationId = id;
    }

    public <T> SingleProtocolServerBuilder protocol(Class<? extends ProtocolDecoder<T>> decoder, Class<? extends ProtocolEncoder<T>> encoder) {
        return new SingleProtocolServerBuilder(decoder, encoder, this);
    }

    public ServerBuilder loadConfiguration(String resource) {
        configurations.add(resource);
        return this;
    }

    public RestServerBuilder rest() {
        return new RestServerBuilder(this);
    }

    protected ConfigurableApplicationContext getContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

        applicationContext.register(ServerConfiguration.class);
        final Properties configuration = new Properties();
        configuration.setProperty("server.host", address().getHostString());
        configuration.setProperty("server.port", String.valueOf(address().getPort()));
        configuration.setProperty("server.id", applicationId);
        applicationContext.getEnvironment().getPropertySources()
                .addFirst(new PropertiesPropertySource("base-configuration", configuration));
        for (String resource : configurations) {
            applicationContext.getEnvironment().getPropertySources()
                    .addLast(new PropertiesPropertySource(resource, loadResource(applicationContext, resource)));
        }

        applicationContext.refresh();

        return applicationContext;
    }

    private Properties loadResource(AnnotationConfigApplicationContext applicationContext, String resource) {
        ResourceLoader loader = new DefaultResourceLoader(applicationContext.getClassLoader());
        PropertiesFactoryBean factoryBean = new PropertiesFactoryBean(applicationId.toLowerCase(), resource,
                applicationContext.getEnvironment(), loader, false);
        try {
            return factoryBean.getObject();
        } catch (Exception e) {
            throw new RuntimeException("Unable to load resource " + resource, e);
        }
    }
}
