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

package com.cumulocity.helloagent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.inventory.InventoryApi;

public class HelloAgent {

    private static final String PROPERTIES_FILENAME = "helloagent.properties";
    private static final String PLATFORM_HOST = "cumulocity.host";
    private static final String TENANT = "cumulocity.tenant";
    private static final String USER = "cumulocity.user";
    private static final String PASSWORD = "cumulocity.password";
    private static final String APPLICATION_KEY = "cumulocity.applicationKey";

    private final Properties configuration;
    private final Logger logger = LoggerFactory.getLogger(HelloAgent.class);

    public HelloAgent() throws IOException {
        InputStream ps = getClass().getClassLoader()
                .getResourceAsStream(PROPERTIES_FILENAME);
        configuration = new Properties();
        configuration.load(ps);
    }

    public void sayHello() {
        ManagedObjectRepresentation mor = new ManagedObjectRepresentation();
        // We should be able to see the name on the HelloWorld! application 
        mor.setName("Hello World!");

        try {
            // Create the object in the database 
            // The returned object is the representation from the platform
            mor = getInventoryAPI().create(mor);
            logger.info("ManagedObject created: {}", mor.getSelf());

        } catch (SDKException e) {
            logger.error("There is a problem connecting to the platform, " +
                    "for help send a support request to support@cumulocity.com " +
                    "and include the following stacktrace: ", e);
        }
    }

    private InventoryApi getInventoryAPI() {
        Platform platform = new PlatformImpl(
                configuration.getProperty(PLATFORM_HOST),
                configuration.getProperty(TENANT),
                configuration.getProperty(USER),
                configuration.getProperty(PASSWORD),
                configuration.getProperty(APPLICATION_KEY));

        return platform.getInventoryApi();
    }
}
