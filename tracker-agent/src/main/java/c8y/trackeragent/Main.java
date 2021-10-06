/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
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

import c8y.trackeragent.devicemapping.DeviceMappingMigrationService;
import c8y.trackeragent.server.Servers;
import c8y.trackeragent.subscription.TenantSubscriptionService;
import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import java.io.IOException;

/**
 * Main class reading the configuration and starting the server.
 */
@ComponentScan(basePackageClasses = Main.class, value = {"c8y.trackeragent", "com.cumulocity"})
@MicroserviceApplication
public class Main {

    @Value("${C8Y.bootstrap.tenant}")
    private String ownerTenant;

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Autowired
    private Servers servers;

    @Autowired
    private TenantSubscriptionService tenantSubscriptionService;

    @Autowired
    private DeviceMappingMigrationService deviceMappingMigrationService;

    public static void main(String[] args) {
        logger.info("tracker-agent is starting.");
        SpringApplication.run(Main.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStart() throws IOException {
        servers.startAll();
        tenantSubscriptionService.subscribeTenants(ownerTenant);
        deviceMappingMigrationService.doMigrationFromPropertyFileToManagedObject();
    }
}