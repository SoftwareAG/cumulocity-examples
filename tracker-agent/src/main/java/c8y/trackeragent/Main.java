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

package c8y.trackeragent;

import c8y.trackeragent.devicebootstrap.TenantBinder;
import c8y.trackeragent.operations.BinariesRepository;
import c8y.trackeragent.operations.DeviceControlRepository;
import c8y.trackeragent.operations.LoggingService;
import c8y.trackeragent.server.Servers;
import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.sdk.client.identity.IdentityApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import java.io.IOException;


/**
 * Main class reading the configuration and starting the server.
 */
//@PropertySources(value = {
//        @PropertySource(value = "classpath:META-INF/tracker/tracker-agent-server.properties", ignoreResourceNotFound = true),
//        @PropertySource(value = "file:${user.home}/.tracker/tracker-agent-server.properties", ignoreResourceNotFound = true),
//        @PropertySource(value = "file:/etc/impact/tracker-agent-server.properties", ignoreResourceNotFound = true)})
@ComponentScan(basePackageClasses = Main.class, value = {"c8y.trackeragent", "com.cumulocity"})
@MicroserviceApplication
public class Main {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Autowired
    private Servers servers;

    @Autowired
    private TenantBinder tenantBinder;

    @Autowired
    private ConfigurableApplicationContext applicationContext;
    
    public static void main(String[] args) {
        logger.info("tracker-agent is starting.");
        SpringApplication.run(Main.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStart() throws IOException {
        servers.startAll();
        tenantBinder.init();
    }
}
