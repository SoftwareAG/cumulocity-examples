package com.cumulocity.agent.server;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.concat;
import static java.lang.System.arraycopy;
import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.cumulocity.agent.server.config.JaxrsServerConfiguration;

public class RestServerBuilder {

    private final Set<Class<?>> resources = new HashSet<Class<?>>();

    private final Set<String> packages = new HashSet<String>();

    private final ServerBuilder builder;

    public RestServerBuilder(ServerBuilder builder) {
        this.builder = builder;
    }

    public RestServerBuilder component(Class<?> component) {
        resources.add(component);
        return this;
    }

    public RestServerBuilder scan(String packageToScan) {
        packages.add(packageToScan);
        return this;
    }

    public Server build() {
    	
        ConfigurableApplicationContext parentContext = builder.getContext();
        parentContext.getBeanFactory().registerSingleton("resourceConfiguration", new ResourceConfig() {
            {
                for (Class<?> component : resources) {
                    register(component);
                }
//                packages(packages.toArray(new String[packages.size()])); does not work
            }
        });

        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();

        applicationContext.setParent(parentContext);
        applicationContext.register(annotatedClasses(JaxrsServerConfiguration.class));
        if (!packages.isEmpty()) {
            applicationContext.scan(from(packages).toArray(String.class));
        }
        applicationContext.refresh();
        return applicationContext.getBean(Server.class);
    }

    private Class[] annotatedClasses(Class... classes) {
        return from(concat(asList(classes), resources)).toArray(Class.class);
    }

}
