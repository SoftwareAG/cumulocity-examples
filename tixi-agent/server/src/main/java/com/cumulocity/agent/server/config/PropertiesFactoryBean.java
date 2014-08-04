package com.cumulocity.agent.server.config;

import static java.lang.String.format;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class PropertiesFactoryBean implements FactoryBean<Properties> {

    private static final Logger log = LoggerFactory.getLogger(PropertiesFactoryBean.class);

    private final Environment environment;

    private final ResourceLoader resourceLoader;

    private final Properties properties;

    private final boolean merge;

    private String fileName;

    public PropertiesFactoryBean(String id, String fileName, Environment environment, ResourceLoader resourceLoader) {
        this(id, fileName, environment, resourceLoader, true);
    }

    public PropertiesFactoryBean(String id, String fileName, Environment environment, ResourceLoader resourceLoader, boolean merge) {
        this.environment = environment;
        this.resourceLoader = resourceLoader;
        this.properties = loadFromStandardLocations(id, fileName);
        this.merge = merge;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public Properties getObject() throws Exception {
        return getProperties();
    }

    @Override
    public Class<?> getObjectType() {
        return Properties.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private Properties loadFromStandardLocations(String id, String fileName) {
        return loadFromStandardLocations(id, environment.getRequiredProperty("user.home"), fileName);
    }

    private Properties loadFromStandardLocations(String id, String userHome, String fileName) {
        return loadFromLocations(
        		format("file:/etc/%s/%s-default.properties", id, fileName),
                format("file:/etc/%s/%s.properties", id, fileName), 
                format("file:%s/.%s/%s.properties", userHome, id, fileName),
                format("classpath:META-INF/%s/%s.properties", id, fileName), 
                format("classpath:META-INF/spring/%s.properties", fileName));
    }

    private Properties loadFromLocations(String... locations) {
        Properties properties = new Properties();
        for (String location : locations) {
            loadFromLocation(properties, location, resourceLoader);
        }
        return properties;
    }

    private void loadFromLocation(Properties properties, String location, ResourceLoader resourceLoader) {
        log.debug("searching for {}", location);
        Resource resource = resourceLoader.getResource(location);
        if (resource.exists()) {
            log.debug("founded {}", location);
            if (!merge) {
                properties.clear();
            }
            try {
                fileName = resource.getURI().getPath();
                properties.load(resource.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException("Error loading properties!", e);
            }
        }
    }
}
