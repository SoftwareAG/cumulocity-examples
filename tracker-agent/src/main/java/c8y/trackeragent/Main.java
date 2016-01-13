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

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import c8y.trackeragent.devicebootstrap.DeviceBinder;
import c8y.trackeragent.server.Servers;
import c8y.trackeragent.utils.TrackerConfiguration;

import com.cumulocity.agent.server.ServerBuilder;
import com.cumulocity.agent.server.feature.ContextFeature;
import com.cumulocity.agent.server.feature.RepositoryFeature;
import com.cumulocity.agent.server.logging.LoggingService;
import com.cumulocity.agent.server.repository.BinariesRepository;
import com.cumulocity.agent.server.repository.DeviceControlRepository;

/**
 * Main class reading the configuration and starting the server.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("c8y.trackeragent")
public class Main {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    @Autowired
    private Servers servers;
    
    @Autowired
    private TrackerConfiguration config;
    
    @Autowired
    private DeviceBinder deviceBinder;
    
    public static void main(String[] args) {
        logger.info("tracker-agent is starting.");
        //@formatter:off
        ServerBuilder.on(8689)
                .application("tracker-agent")
                .logging("tracker-agent-server-logging")
                .loadConfiguration("tracker-agent-server")
                .enable(Main.class)
                .enable(ContextFeature.class)
                .enable(RepositoryFeature.class)
                .useWebEnvironment(false)
                .run(args);
        //@formatter:on
    }
    
    @PostConstruct
    public void onStart() {
        servers.startAll();
        deviceBinder.init();
    }
    
    @Bean
    @Autowired
    public LoggingService loggingService(DeviceControlRepository deviceControl, BinariesRepository binaries, 
            @Value("${C8Y.log.file.path}") String logfile, @Value("${C8Y.log.timestamp.format:}") String timestampFormat,
            @Value("${C8Y.application.id}") String applicationId) {
        return new LoggingService(deviceControl, binaries, logfile, timestampFormat, applicationId);
    }
}
