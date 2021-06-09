/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent_it.config;

import c8y.trackeragent.Main;
import c8y.trackeragent.devicebootstrap.TenantBinder;
import c8y.trackeragent.server.Servers;
import com.cumulocity.agent.server.feature.ContextFeature;
import com.cumulocity.agent.server.feature.RepositoryFeature;
import com.cumulocity.agent.server.logging.LoggingService;
import com.cumulocity.agent.server.repository.BinariesRepository;
import com.cumulocity.agent.server.repository.DeviceControlRepository;
import com.cumulocity.sms.client.SmsMessagingApi;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "c8y.trackeragent" }, excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = Main.class) })
@Import({RepositoryFeature.class, ContextFeature.class})
@PropertySource(value = { "file:/etc/tracker-agent/tracker-agent-server.properties" })
public class ServerConfiguration {

    @Autowired
    Servers servers;
    
    @Autowired
    TenantBinder tenantBinder;
    
    @Bean
    @Autowired
    public LoggingService loggingService(DeviceControlRepository deviceControl, BinariesRepository binaries, 
            @Value("${C8Y.log.file.path}") String logfile, @Value("${C8Y.log.timestamp.format:}") String timestampFormat,
            @Value("${C8Y.application.id}") String applicationId) {
        return new LoggingService(deviceControl, binaries, logfile, timestampFormat, applicationId);
    }
    
    @Bean
    public SmsMessagingApi outgoingMessagingClient() {
        return Mockito.mock(SmsMessagingApi.class);
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @PostConstruct
    public void startServer() throws IOException {
        servers.startAll();
        tenantBinder.init();
    }
}
